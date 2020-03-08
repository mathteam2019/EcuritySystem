<style lang="scss">
  .device-classify {
    .form-section {
      height: 100%;
    }
  }
  .img-rotate{
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }
</style>
<template>
  <div class="device-classify">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card v-show="!isLoading" class="main-without-tab">
      <div v-show="pageStatus==='list'" class="h-100 flex-column" :class="pageStatus === 'list'?'d-flex':''">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-classify-item.classify')">
                  <b-form-input v-model="filterOption.categoryName"/>
                </b-form-group>
              </b-col>

              <b-col cols="4">
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-classify-item.super-classify')">
                  <b-form-input v-model="filterOption.parentCategoryName"/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_category_export')">
              <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()" :disabled="checkPermItem('device_category_print')">
              <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default" :disabled="checkPermItem('device_category_create')">
              <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
            </b-button>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="deviceClassifyTable"
                :api-url="deviceClassifyTableItems.apiUrl"
                :fields="deviceClassifyTableItems.fields"
                :http-fetch="deviceClassifyTableHttpFetch"
                :per-page="deviceClassifyTableItems.perPage"
                pagination-path="pagination"
                track-by="categoryId"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.categoryNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm" :disabled="props.rowData.status === '1000000701' || checkPermItem('device_category_modify')"
                            variant="primary default btn-square">
                    <i class="icofont-edit"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)" :disabled="checkPermItem('device_category_update_status')"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"/>
                  </b-button>
                  <b-button
                    @click="onAction('inactivate',props.rowData)"
                    v-if="props.rowData.status==='1000000701'"
                    size="sm"
                    :disabled="props.rowData.parentCategoryId === 0 || checkPermItem('device_category_update_status')"
                    variant="warning default btn-square" >
                    <i class="icofont-ban"/>
                  </b-button>
                  <b-button
                    size="sm"
                    @click="onAction('delete',props.rowData)"
                    variant="danger default btn-square"
                    :disabled="props.rowData.status === '1000000701' || checkPermItem('device_category_delete')">
                    <i class="icofont-bin"/>
                  </b-button>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
                :initial-per-page="deviceClassifyTableItems.perPage"
                @onUpdatePerPage="deviceClassifyTableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>

      </div>
      <div v-if="pageStatus === 'create' || pageStatus === 'edit' " class="h-100 d-flex flex-column">
        <b-row class="form-section d-flex">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device-number')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input
                    :state="!$v.classifyForm.categoryNumber.$invalid"
                    v-model="classifyForm.categoryNumber"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.classifyForm.categoryNumber.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input
                    :state="!$v.classifyForm.categoryName.$invalid"
                    v-model="classifyForm.categoryName"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.classifyForm.categoryName.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device-number')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="classifyForm.parentCategoryNumber" disabled/>
                  <div class="invalid-feedback d-block">
                    {{ "&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-select :disabled="pageStatus === 'edit'"
                    :state="!$v.classifyForm.parentCategoryId.$invalid"
                    v-model="classifyForm.parentCategoryId" :options="parentClassifyOptions" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.classifyForm.parentCategoryId.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.remark')}}
                  </template>
                  <b-form-textarea :rows="3" v-model="classifyForm.note"/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12 text-right mt-3 " class="align-self-end">
            <b-button size="sm" @click="saveCategoryItem()" variant="info default"><i class="icofont-save"/>
              {{$t('device-management.save')}}
            </b-button>
            <b-button @click="onAction('delete',classifyForm)" size="sm" variant="danger default" :disabled="checkPermItem('device_category_delete')"
                      v-if="pageStatus !== 'create'">
              <i class="icofont-bin"/> {{$t('system-setting.delete')}}
            </b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
              class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
            </b-button>
          </b-col>
          <div v-if="getLocale()==='zh'" class="position-absolute" style="left: 3%;bottom: 17%">
            <img v-if="classifyForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="classifyForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
          </div>
          <div v-if="getLocale()==='en'" class="position-absolute" style="left: 3%;bottom: 17%">
            <img v-if="classifyForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
            <img v-else-if="classifyForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
          </div>
        </b-row>
      </div>
      <div v-if="pageStatus==='show'" class="h-100 d-flex flex-column">
        <b-row class="form-section d-flex">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device-number')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="classifyForm.categoryNumber"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="classifyForm.categoryName"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device-number')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-model="classifyForm.parentCategoryNumber"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-select v-if="classifyForm.parentCategoryId > 0" disabled v-model="classifyForm.parentCategoryId" :options="parentClassifyOptions" plain/>
                  <b-form-input v-if="classifyForm.parentCategoryId === 0" disabled v-model="classifyForm.parentCategoryNumber"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.remark')}}
                  </template>
                  <b-form-textarea :rows="3" v-model="classifyForm.note"/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12 text-right mt-3 " class="align-self-end">
            <b-button v-if="classifyForm.status === '1000000701' && classifyForm.parentCategoryId !== 0" :disabled="checkPermItem('device_category_update_status')" @click="onAction('inactivate',classifyForm)" size="sm"
                      variant="warning default">
              <i class="icofont-ban"/> {{$t('system-setting.status-inactive')}}
            </b-button>
            <b-button v-if="classifyForm.status === '1000000702'" @click="onAction('activate',classifyForm)" :disabled="checkPermItem('device_category_update_status')" size="sm" variant="success default">
              <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
            </b-button>
            <b-button v-if="classifyForm.status === '1000000702'" @click="onAction('delete',classifyForm)" size="sm" :disabled="checkPermItem('device_category_delete')"
                      variant="danger default">
              <i class="icofont-bin"/> {{$t('system-setting.delete')}}
            </b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
              class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
            </b-button>
          </b-col>
          <div v-if="getLocale()==='zh'" class="position-absolute" style="left: 3%;bottom: 17%">
            <img v-if="classifyForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="classifyForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
          </div>
          <div v-if="getLocale()==='en'" class="position-absolute" style="left: 3%;bottom: 17%">
            <img v-if="classifyForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
            <img v-else-if="classifyForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
          </div>
        </b-row>
      </div>
    </b-card>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('device-management.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {downLoadFileFromServer, getApiManager, printFileFromServer} from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getLocale} from "../../../utils";

  const {required} = require('vuelidate/lib/validators');

  let getParentSerialName = (categoryData, categoryId) => {
    let parentSerialNumber = null;
    if (categoryData == null || categoryData.length === 0)
      return parentSerialNumber;
    categoryData.forEach(siteItem => {
      if (siteItem.categoryId === categoryId)
        parentSerialNumber = siteItem.categoryNumber;
    });
    return parentSerialNumber;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mixins: [validationMixin],
    validations: {
      classifyForm: {
        categoryNumber: {
          required
        },
        categoryName: {
          required
        },
        parentCategoryId: {
          required
        }
      }
    },
    mounted() {
      this.getCategoryData();
      this.$refs.deviceClassifyTable.$parent.transform = this.transformCategoryTable.bind(this);
    },
    data() {
      return {
        isLoading: false,
        categoryData: [],
        submitted: false,
        parentClassifyOptions: [],
        pageStatus: 'list',
        selectedStatus: 'all',
        filterOption: {
          categoryName: '',
          status: null,
          parentCategoryName: ''
        },
        classifyForm: {
          categoryNumber: null,
          categoryName: null,
          parentCategoryId: null,
          parentCategoryNumber: "",
          categoryId: 0,
          note: null
        },

        deviceClassifyTableItems: {
          apiUrl: `${apiBaseUrl}/device-management/device-classify/category/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'categoryId',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'categoryNumber',
              title: this.$t('device-management.device-classify-item.device-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'categoryName',
              title: this.$t('device-management.device-classify-item.classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              title: this.$t('device-management.active'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000701": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "1000000702": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }

            },
            {
              name: 'parentCategoryNumber',
              title: this.$t('device-management.device-classify-item.super-classify-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'parentCategoryName',
              title: this.$t('device-management.device-classify-item.super-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'note',
              title: this.$t('system-setting.remarks'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '10%'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '210px'
            }
          ]
        },

        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
      }
    },
    methods: {
      getLocale() {
        return getLocale();
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton(){
        let checkedAll = this.$refs.deviceClassifyTable.checkedAllStatus;
        let checkedIds = this.$refs.deviceClassifyTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-classify/category`;
        downLoadFileFromServer(link,params,'category');
      },
      onPrintButton(){
        let checkedAll = this.$refs.deviceClassifyTable.checkedAllStatus;
        let checkedIds = this.$refs.deviceClassifyTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-classify/category`;
        printFileFromServer(link,params);
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      getCategoryData() {
        getApiManager().post(`${apiBaseUrl}/device-management/device-classify/category/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.categoryData = data;
              break;
          }
        });
      },
      onSearchButton() {
        this.$refs.deviceClassifyTable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          categoryName: '',
          status: null,
          parentCategoryName: ''
        };
      },
      onAction(value,data = null) {
        switch (value) {
          case 'create':
            this.initialize();
            this.pageStatus = 'create';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'edit':
            this.initialize(data);
            this.pageStatus = 'edit';
            break;
          case 'activate':
            this.initialize(data);
            this.updateItemStatus('1000000701');
            break;
          case 'show':
            this.initialize(data);
            this.pageStatus = 'show';
            break;
          case 'inactivate':
            this.initialize(data);
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.initialize(data);
            this.$refs['modal-delete'].show();
            break;
        }
      },
      transformCategoryTable(response) {
        let transformed = {};

        let data = response.data;

        transformed.pagination = {
          total: data.total,
          per_page: data.per_page,
          current_page: data.current_page,
          last_page: data.last_page,
          from: data.from,
          to: data.to
        };

        transformed.data = [];
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          temp.parentCategoryName = temp.parent != null ? temp.parent.categoryName : null;
          temp.parentCategoryNumber = temp.parent != null ? temp.parent.categoryNumber : null;
          transformed.data.push(temp);
        }

        return transformed
      },
      deviceClassifyTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.deviceClassifyTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filterOption
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.deviceClassifyTable.changePage(page);
      },

      initialize(data = null) {
        if (data == null)
          this.classifyForm = {
            categoryNumber: null,
            categoryName: null,
            parentCategoryId: null,
            parentCategoryNumber: "",
            categoryId: 0,
            note: ""
          };
        else
          this.classifyForm = data;
        this.submitted = false;
      },

      saveCategoryItem() {
        this.submitted = true;
        this.$v.classifyForm.$touch();
        if (this.$v.classifyForm.$invalid) {
          return;
        }
        this.isLoading = true;
        let finalLink = this.classifyForm.categoryId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-classify/category/` + finalLink, this.classifyForm)
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.category-added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.getCategoryData();
                this.$refs.deviceClassifyTable.reload();

                break;
              case responseMessages['used-category-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-category-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-category-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-category-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
            this.isLoading = false;
          })
          .catch((error) => {
            this.isLoading = false;
          });

      },
      //update status
      updateItemStatus(statusValue) {
        let categoryId = this.classifyForm.categoryId;
        if (categoryId === 0)
          return false;

        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-classify/category/update-status`, {
            categoryId: categoryId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.classifyForm.categoryId > 0)
                  this.classifyForm.status = statusValue;
                if (this.pageStatus === 'list')
                  this.$refs.deviceClassifyTable.reload();
                break;
              case "has_archive_template": // already used
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.category-has-archive-template`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-children']: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.category-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
      },

      //remove category
      removeItem() {
        let categoryId = this.classifyForm.categoryId;
        if (categoryId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-classify/category/delete`, {
            categoryId: categoryId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.category-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.deviceClassifyTable.refresh();
                this.getCategoryData();
                if (this.classifyForm.categoryId > 0)
                  initialize();

                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.category-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case "has_archive_template": // already used
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.category-has-archive-template`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
        this.$refs['modal-delete'].hide();
      },


    },
    watch: {
      'deviceClassifyTableItems.perPage': function (newVal) {
        this.$refs.deviceClassifyTable.refresh();
      },
      'classifyForm.parentCategoryId': function (newVal) {
        this.classifyForm.parentCategoryNumber = getParentSerialName(this.categoryData, newVal);
        if (this.classifyForm.parentCategoryNumber === null)
          this.classifyForm.parentCategoryNumber = this.$t('system-setting.none');
      },

      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.parentClassifyOptions = [];
        if (newVal.length === 0) {
          this.parentClassifyOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          this.parentClassifyOptions = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }

      },
    }
  }
</script>

