<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div v-if="pageStatus==='list'" class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.template')">
                  <b-form-input v-model="filterOption.templateName"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="filterOption.categoryId" :options="categoryFilterData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_template_export')">
              <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()" :disabled="checkPermItem('device_template_print')">
              <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default" :disabled="checkPermItem('device_template_create')">
              <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
            </b-button>
          </b-col>
        </b-row>
        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="vuetable"
                :fields="vuetableItems.fields"
                :api-url="vuetableItems.apiUrl"
                :http-fetch="vuetableHttpFetch"
                :per-page="vuetableItems.perPage"
                pagination-path="pagination"
                track-by="archivesTemplateId"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped text-center"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.archivesTemplateNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm" :disabled="props.rowData.status === '1000000701' || checkPermItem('device_template_modify')"
                            variant="primary default btn-square">
                    <i class="icofont-edit"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)" :disabled="checkPermItem('device_template_update_status')"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='1000000701'" :disabled="checkPermItem('device_template_update_status')"
                    size="sm" @click="onAction('inactivate',props.rowData)"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"></i>
                  </b-button>
                  <b-button @click="onAction('delete',props.rowData)"
                            size="sm" :disabled="props.rowData.status === '1000000701' || checkPermItem('device_template_delete')"
                            variant="danger default btn-square">
                    <i class="icofont-bin"></i>
                  </b-button>

                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
                :initial-per-page="vuetableItems.perPage"
                @onUpdatePerPage="vuetableItems.perPage = Number($event)"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>
      </div>
      <div v-show="pageStatus !== 'list'" class="h-100">
        <div  class="form-section d-flex flex-column">
          <b-row>
            <b-col xxs="12" md="4" lg="3">
              <b-form-group>
                <template slot="label">{{$t('device-management.template-number')}}<span class="text-danger">*</span>
                </template>
                <b-form-input type="text" v-model="basicForm.archivesTemplateNumber"
                              :state="!$v.basicForm.archivesTemplateNumber.$dirty ? null : !$v.basicForm.archivesTemplateNumber.$invalid"
                              :placeholder="$t('device-management.template-number-placeholder')"/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.archivesTemplateNumber.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group>
                <template slot="label">{{$t('device-management.template')}}<span class="text-danger">*</span>
                </template>
                <b-form-input type="text" v-model="basicForm.templateName" :state="!$v.basicForm.templateName.$dirty ? null : !$v.basicForm.templateName.$invalid"
                              :placeholder="$t('device-management.template-name-placeholder')"/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.templateName.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group>
                <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                </template>
                <b-form-select v-model="basicForm.categoryId" :options="categorySelectOptions"
                               :disabled="pageStatus === 'show'" :state="!$v.basicForm.categoryId.$dirty ? null : !$v.basicForm.categoryId.$invalid"
                               :placeholder="$t('device-management.device-classify-placeholder')" plain/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.categoryId.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group :label="$t('device-management.manufacture')">
                <b-form-select v-model="basicForm.manufacturer" :options="manufacturerOptions"
                               :disabled="pageStatus === 'show'" plain/>
              </b-form-group>
              <b-form-group :label="$t('device-management.device-model')">
                <b-form-input type="text" v-model="basicForm.originalModel"
                              :placeholder="$t('device-management.origin-model-placeholder')"/>
              </b-form-group>
            </b-col>
            <b-col xxs="12" md="8" lg="9">
              <b-row>
                <b-col v-if="pageStatus!=='show'" cols="12" class="d-flex justify-content-between mb-2">
                  <label class="font-weight-bold" style="line-height: 28px">{{$t('device-management.document-template.device-show-list')}}</label>
                  <b-button size="sm" variant="success default">
                    <i class="icofont-plus"></i> {{$t('device-management.document-template.add-indicator')}}
                  </b-button>
                </b-col>
                <b-col v-if="pageStatus!=='show'" cols="12">
                  <b-row>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.name')">
                        <b-form-input type="text" v-model="indicatorForm.indicatorsName"/>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.unit')">
                        <b-form-input type="text" v-model="indicatorForm.indicatorsUnit"/>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.isNull')">
                        <b-form-select v-model="indicatorForm.isNull" :options="yesNoOptions" plain/>
                      </b-form-group>
                    </b-col>
                    <b-col class="d-flex">
                      <b-button size="sm" variant="success default" class="align-self-center" @click="onSaveIndicator()" :disabled="checkPermItem('device_indicator_create')">
                        <i class="icofont-save"></i> {{$t('permission-management.save')}}
                      </b-button>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col cols="12" class="table-responsive text-center">

                  <vuetable
                    ref="indicatorTable"
                    :api-mode="false"
                    :fields="indicatorTableItems.fields"
                    :data-manager="indicatorTableDataManager"
                    :per-page="indicatorTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onIndicatorTablePaginationData"
                    class="table-striped text-center"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary">{{ props.rowData.indicatorsName }}</span>
                    </div>
                    <div slot="required" slot-scope="props">
                      <b-button v-if="props.rowData.isNull === 'yes'" :disabled="checkPermItem('device_indicator_update_is_null')"
                                size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                variant="success default">
                        <i class="icofont-check-alt"></i>&nbsp;{{$t('device-management.document-template.yes')}}
                      </b-button>
                      <b-button v-if="props.rowData.isNull === 'no'" :disabled="checkPermItem('device_indicator_update_is_null')"
                                size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                variant="light default">
                        <i class="icofont-close-line"></i>&nbsp;{{$t('device-management.document-template.no')}}
                      </b-button>
                    </div>
                    <div slot="action" slot-scope="props">
                      <b-button
                        size="sm" @click="onRemoveIndicator(props.rowData,props.rowIndex)" :disabled="checkPermItem('device_indicator_delete')"
                        variant="danger default btn-square">
                        <i class="icofont-bin"></i>
                      </b-button>
                    </div>
                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="indicatorTablePagination"
                    @vuetable-pagination:change-page="onIndicatorTableChangePage"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-col>
          </b-row>
          <b-row class="flex-grow-1 align-items-end">
            <b-col cols="12 text-right mt-3">
              <b-button size="sm" v-if="pageStatus !== 'show'" @click="onSaveTemplate()" variant="info default"><i
                class="icofont-save"></i>
                {{$t('device-management.save')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000701'" @click="onAction('inactivate',basicForm)" size="sm"
                        variant="warning default" :disabled="checkPermItem('device_template_update_status')">
                <i class="icofont-ban"></i> {{$t('system-setting.status-inactive')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000702' && pageStatus !=='create'"
                        @click="onAction('activate',basicForm)" size="sm" :disabled="checkPermItem('device_template_update_status')"
                        variant="success default">
                <i class="icofont-check-circled"></i> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000702'&& pageStatus !=='create'"
                        @click="onAction('delete',basicForm)" size="sm" :disabled="checkPermItem('device_template_delete')"
                        variant="danger default">
                <i class="icofont-bin"></i> {{$t('system-setting.delete')}}
              </b-button>
              <b-button size="sm" @click="onAction('show-list')" variant="info default"><i
                class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
              </b-button>
            </b-col>
          </b-row>
          <div class="position-absolute" style="left: 3%;bottom: 10%">
            <img v-if="basicForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="basicForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
          </div>
        </div>
      </div>

    </b-card>

    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.delete-prompt')}}
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
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {downLoadFileFromServer, getApiManager, printFileFromServer} from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDicDataByDicIdForOptions} from "../../../utils";

  const {required} = require('vuelidate/lib/validators');

  let getManufacturerName = (options, value) => {
    let name = null;
    if (options == null || options.length === 0)
      return name;
    options.forEach(option => {
      if (option.value === value)
        name = option.text;
    });
    return name;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
    },
    mixins: [validationMixin],
    validations: {
      basicForm: {
        templateName: {
          required
        },
        archivesTemplateNumber: {
          required
        },
        categoryId: {
          required
        }
      }
    },
    data() {
      return {
        submitted: false,
        categoryData: [],
        pageStatus: 'list',
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
        filterOption: {
          templateName: '',
          status: null,
          categoryId: null
        },
        basicForm: {
          templateName: '',
          archivesTemplateNumber: '',
          categoryId: '',
          manufacturer: '',
          originalModel: '',
          archiveIndicatorsList: [],
          note: '',
          archivesTemplateId: 0
        },
        categoryFilterData: [],
        categorySelectOptions: [],
        manufacturerOptions: [],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/device-management/document-template/archive-template/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archivesTemplateId',
              sortField: 'archivesTemplateId',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'archivesTemplateNumber',
              title: this.$t('device-management.template-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'templateName',
              sortField: 'templateName',
              title: this.$t('device-management.template'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
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
              name: 'category',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturerName',
              sortField: 'manufacturer',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'originalModel',
              sortField: 'originalModel',
              title: this.$t('device-management.device-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
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

        yesNoOptions: [
          {value: '1000000601', text: this.$t('system-setting.parameter-setting.yes')},
          {value: '1000000602', text: this.$t('system-setting.parameter-setting.no')},
        ],
        indicatorForm: {
          indicatorsId: 0,
          indicatorsName: null,
          indicatorsUnit: null,
          isNull: "1000000601"
        },

        indicatorTableItems: {
          fields: [
            {
              name: 'indicatorsId',
              sortField: 'indicatorsId',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'list-item-heading'
            },
            {
              name: '__slot:number',
              sortField: 'indicatorsName',
              title: this.$t('device-management.document-template.indicator'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '40%'
            },
            {
              name: 'indicatorsUnit',
              sortField: 'indicatorsUnit',
              title: this.$t('device-management.document-template.unit'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: '__slot:required',
              sortField: 'isNull',
              title: this.$t('device-management.document-template.required'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:action',
              title: this.$t('device-management.action'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage: 5
        },
        indicatorData: [],
      }
    },
    mounted() {
      this.getCategoryData();
      this.getManufacturerOptions();
      this.$refs.vuetable.$parent.transform = this.transformTemplateTable.bind(this);
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManufacturerOptions(){
        this.manufacturerOptions =  getDicDataByDicIdForOptions(9);
      },
      onExportButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-template/archive-template`;
        downLoadFileFromServer(link,params,'document-template');
      },
      onPrintButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-template/archive-template/pdf`;
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
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          templateName: '',
          status: null,
          categoryId: null
        };
      },
      onAction(value, data = null) {

        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            this.initialize(data);
            break;
          case 'edit':
            this.pageStatus = 'edit';
            this.initialize(data);
            break;
          case 'show':
            this.pageStatus = 'show';
            this.initialize(data);
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'activate':
            this.initialize(data,false);
            this.updateItemStatus('1000000701');
            break;
          case 'inactivate':
            this.initialize(data,false);
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.initialize(data,false);
            this.$refs['modal-delete'].show();
            break;
        }
      },
      transformTemplateTable(response) {
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
          temp.category = temp.deviceCategory.categoryName;
          temp.manufacturerName = getManufacturerName(this.manufacturerOptions, temp.manufacturer);
          transformed.data.push(temp);
        }
        return transformed
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.filterOption
        });
      },
      initialize(data = null,isUpdated = true) {
        this.indicatorForm = {
          indicatorsId: 0,
          indicatorsName: null,
          indicatorsUnit: null,
          isNull: "1000000602"
        };
        if (data == null) {
          this.basicForm = {
            templateName: '',
            archivesTemplateNumber: '',
            categoryId: '',
            manufacturer: '',
            originalModel: '',
            archiveIndicatorsList: [],
            note: '',
            archivesTemplateId: 0
          };
          this.indicatorData = [];
        }
        else {
          this.basicForm = data;
          if (isUpdated) {
            let items = [];
            this.basicForm.archiveIndicatorsList.forEach((item) => {
              items.push({
                indicatorsId: item.indicatorsId,
                indicatorsName: item.indicatorsName,
                indicatorsUnit: item.indicatorsUnit,
                isNull: item.isNull
              });
              this.indicatorData = items;
            });
          }
        }
        this.submitted = false;
      },
      //save document template Item
      onSaveTemplate() {
        this.submitted = true;
        this.$v.basicForm.$touch();
        if (this.$v.basicForm.$invalid) {
          return;
        }
        this.indicatorData.forEach((item) => {
          if (item.indicatorsId > 0)
            this.basicForm.archiveIndicatorsList.push(item.indicatorsId)
        });
        if (this.basicForm.archiveIndicatorsList.length === 0) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.not-have-indicators`), {
            duration: 3000,
            permanent: false
          });
          return false;
        }
        let finalLink = this.basicForm.archivesTemplateId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/` + finalLink, {
            templateName: this.basicForm.templateName,
            archivesTemplateNumber: this.basicForm.archivesTemplateNumber,
            categoryId: this.basicForm.categoryId,
            manufacturer: this.basicForm.manufacturer,
            originalModel: this.basicForm.originalModel,
            archiveIndicatorsList: this.indicatorData,
            note: '',
            archivesTemplateId: this.basicForm.archivesTemplateId
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
      },

      //update status
      updateItemStatus(statusValue) {
        let templateId = this.basicForm.archivesTemplateId;
        if (templateId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/update-status`, {
            archivesTemplateId: templateId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.basicForm.archivesTemplateId > 0)
                  this.basicForm.status = statusValue;
                if (this.pageStatus === 'list')
                  this.$refs.vuetable.refresh();
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
      },
      //remove category
      removeItem() {
        let templateId = this.basicForm.archivesTemplateId;
        if (templateId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/delete`, {
            archivesTemplateId: templateId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.vuetable.refresh();
                if (this.basicForm.archivesTemplateId > 0)
                  initialize();
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
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

      //todo for indicatorTable points.

      onIndicatorTablePaginationData(paginationData) {
        this.$refs.indicatorTablePagination.setPaginationData(paginationData)
      },
      onIndicatorTableChangePage(page) {
        this.$refs.indicatorTable.changePage(page)
      },
      indicatorTableDataManager(sortOrder, pagination) {
        let local = this.indicatorData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }
        pagination = this.$refs.indicatorTable.makePagination(
          local.length,
          this.indicatorTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.indicatorTableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      },
      onSaveIndicator() {
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/create`, this.indicatorForm)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorForm.indicatorsId = data;
                this.indicatorData.push(this.indicatorForm);
                this.indicatorForm = {
                  indicatorsId: 0,
                  indicatorsName: null,
                  indicatorsUnit: null,
                  isNull: "1000000601"
                };
                break;
            }
          })
          .catch((error) => {
          });
      },
      onSwitchIsNull(item, index) {
        let value = item.isNull==='1000000601'?'1000000602':'1000000601';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/update-isnull`, {
            indicatorsId: item.indicatorsId,
            isNull: value
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorData[index].isNull = value;
                break;
            }
          })
          .catch((error) => {
          });
      },
      onRemoveIndicator(item, index) {
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/delete`, {
            indicatorsId: item.indicatorsId
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorData.splice(index,1);
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },

      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.categorySelectOptions = [];
        if (newVal.length === 0) {
          this.categorySelectOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          this.categorySelectOptions = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.categoryFilterData = JSON.parse(JSON.stringify(this.categorySelectOptions));
        this.categoryFilterData.push({value: null, text: `${this.$t('permission-management.all')}`})
      },
      indicatorData(newVal, oldVal) {
        if(this.$refs.indicatorTable!== undefined)
          this.$refs.indicatorTable.refresh();
      }
    }
  }
</script>
