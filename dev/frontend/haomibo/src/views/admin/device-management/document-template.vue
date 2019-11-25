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
            <b-button size="sm" class="ml-2" variant="outline-info default">
              <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default">
              <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default">
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
                @vuetable:pagination-data="onPaginationData"
                class="table-striped text-center"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.archivesTemplateNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm" :disabled="props.rowData.status === 'active'"
                            variant="primary default btn-square" >
                    <i class="icofont-edit"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='inactive'"
                    size="sm"  @click="onAction('activate',props.rowData)"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='active'"
                    size="sm" @click="onAction('inactivate',props.rowData)"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"></i>
                  </b-button>
                  <b-button @click="onAction('delete',props.rowData)"
                    size="sm" :disabled="props.rowData.status === 'active'"
                    variant="danger default btn-square" >
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
      <div v-if="pageStatus==='create' || pageStatus==='edit'" class="form-section d-flex flex-column">
        <b-row>
          <b-col xxs="12" md="4" lg="3">
            <b-form-group>
              <template slot="label">{{$t('device-management.template-number')}}<span class="text-danger">*</span>
              </template>
              <b-form-input type="text" v-model="basicForm.archivesTemplateNumber"
                            :placeholder="$t('device-management.template-number-placeholder')"/>
              <div class="invalid-feedback d-block">
                {{ (submitted && !$v.basicForm.archivesTemplateNumber.required) ?
                $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
              </div>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('device-management.template')}}<span class="text-danger">*</span>
              </template>
              <b-form-input type="text" v-model="basicForm.templateName"
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
                             :placeholder="$t('device-management.device-classify-placeholder')" plain/>
              <div class="invalid-feedback d-block">
                {{ (submitted && !$v.basicForm.categoryId.required) ?
                $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
              </div>
            </b-form-group>
            <b-form-group :label="$t('device-management.manufacture')">
              <b-form-select v-model="basicForm.manufacturer" :options="manufacturerOptions" plain/>
            </b-form-group>
            <b-form-group :label="$t('device-management.device-model')">
              <b-form-input type="text" v-model="basicForm.originalModel"
                            :placeholder="$t('device-management.origin-model-placeholder')"/>
            </b-form-group>
          </b-col>
          <b-col xxs="12" md="8" lg="9">
            <b-row>
              <b-col cols="12" class="d-flex justify-content-between mb-2">
                <label class="font-weight-bold" style="line-height: 28px">{{$t('device-management.document-template.device-show-list')}}</label>
                <b-button size="sm" variant="success default">
                  <i class="icofont-plus"></i> {{$t('device-management.document-template.add-indicator')}}
                </b-button>
              </b-col>
              <b-col cols="12" class="table-responsive text-center">

                <vuetable
                  ref="deviceShowingTable"
                  :api-mode="false"
                  :fields="deviceShowingTableItems.fields"
                  :data-manager="deviceShowingTableDataManager"
                  :per-page="deviceShowingTableItems.perPage"
                  pagination-path="pagination"
                  @vuetable:pagination-data="onDeviceShowingTablePaginationData"
                  class="table-striped text-center"
                >
                  <div slot="number" slot-scope="props">
                    <span class="cursor-p text-primary">{{ props.rowData.number }}</span>
                  </div>
                  <div slot="required" slot-scope="props">
                    <b-button v-if="props.rowData.status"
                              size="xs"
                              variant="success default">
                      <i class="icofont-check-alt"></i>&nbsp;{{$t('device-management.document-template.yes')}}
                    </b-button>
                    <b-button v-if="!props.rowData.status"
                              size="xs"
                              variant="light default">
                      <i class="icofont-close-line"></i>&nbsp;{{$t('device-management.document-template.no')}}
                    </b-button>
                  </div>
                  <div slot="action" slot-scope="props">
                    <b-button
                      size="sm"
                      variant="danger default btn-square">
                      <i class="icofont-bin"></i>
                    </b-button>
                  </div>
                </vuetable>
                <vuetable-pagination-bootstrap
                  ref="deviceShowingTablePagination"
                  @vuetable-pagination:change-page="onDeviceShowingTableChangePage"
                ></vuetable-pagination-bootstrap>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row class="flex-grow-1 align-items-end">
          <b-col cols="12 text-right mt-3">
            <b-button size="sm" @click="onSaveTemplate()" variant="info default"><i class="icofont-save"></i> {{$t('device-management.save')}}
            </b-button>
            <b-button size="sm" v-if="pageStatus === 'edit'" variant="success default"><i class="icofont-check-circled"></i>
              {{$t('device-management.active')}}
            </b-button>
            <b-button size="sm" v-if="pageStatus === 'edit'" @click="onAction('delete',basicForm)" variant="danger default"><i class="icofont-bin"></i> {{$t('device-management.delete')}}
            </b-button>
            <b-button size="sm" @click="onAction('show-list')" variant="info default" ><i
              class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
            </b-button>
          </b-col>
        </b-row>
        <div class="position-absolute" style="left: 3%;bottom: 10%">
          <img v-if="basicForm.status === 'inactive'" src="../../../assets/img/no_active_stamp.png">
          <img v-else-if="basicForm.status === 'active'" src="../../../assets/img/active_stamp.png">
        </div>
      </div>
      <div v-if="pageStatus==='show'" class="form-section d-flex flex-column">
        <b-row>
          <b-col xxs="12" md="4" lg="3">
            <b-form-group>
              <template slot="label">{{$t('device-management.template-number')}}<span class="text-danger">*</span>
              </template>
              <b-form-input type="text" v-model="basicForm.archivesTemplateNumber"
                            :placeholder="$t('device-management.template-number-placeholder')"/>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('device-management.template')}}<span class="text-danger">*</span>
              </template>
              <b-form-input type="text" v-model="basicForm.templateName"
                            :placeholder="$t('device-management.template-name-placeholder')"/>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
              </template>
              <b-form-select v-model="basicForm.categoryId" :options="categorySelectOptions"
                             :placeholder="$t('device-management.device-classify-placeholder')" disabled plain/>
            </b-form-group>
            <b-form-group :label="$t('device-management.manufacture')">
              <b-form-select v-model="basicForm.manufacturer" :options="manufacturerOptions" disabled plain/>
            </b-form-group>
            <b-form-group :label="$t('device-management.device-model')">
              <b-form-input type="text" v-model="basicForm.originalModel"
                            :placeholder="$t('device-management.origin-model-placeholder')" />
            </b-form-group>
          </b-col>
          <b-col xxs="12" md="8" lg="9">
            <b-row>
              <b-col cols="12" class="d-flex justify-content-between mb-2">
                <label class="font-weight-bold" style="line-height: 28px">{{$t('device-management.document-template.device-show-list')}}</label>
                <b-button size="sm" variant="success default">
                  <i class="icofont-plus"></i> {{$t('device-management.document-template.add-indicator')}}
                </b-button>
              </b-col>
              <b-col cols="12" class="table-responsive text-center">

                <vuetable
                  ref="deviceShowingTable"
                  :api-mode="false"
                  :fields="deviceShowingTableItems.fields"
                  :data-manager="deviceShowingTableDataManager"
                  :per-page="deviceShowingTableItems.perPage"
                  pagination-path="pagination"
                  @vuetable:pagination-data="onDeviceShowingTablePaginationData"
                  class="table-striped text-center"
                >
                  <div slot="number" slot-scope="props">
                    <span class="cursor-p text-primary">{{ props.rowData.number }}</span>
                  </div>
                  <div slot="required" slot-scope="props">
                    <b-button v-if="props.rowData.status"
                              size="xs"
                              variant="success default">
                      <i class="icofont-check-alt"></i>&nbsp;{{$t('device-management.document-template.yes')}}
                    </b-button>
                    <b-button v-if="!props.rowData.status"
                              size="xs"
                              variant="light default">
                      <i class="icofont-close-line"></i>&nbsp;{{$t('device-management.document-template.no')}}
                    </b-button>
                  </div>
                  <div slot="action" slot-scope="props">
                    <b-button
                      size="sm"
                      variant="danger default btn-square">
                      <i class="icofont-bin"></i>
                    </b-button>
                  </div>
                </vuetable>
                <vuetable-pagination-bootstrap
                  ref="deviceShowingTablePagination"
                  @vuetable-pagination:change-page="onDeviceShowingTableChangePage"
                ></vuetable-pagination-bootstrap>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row class="flex-grow-1 align-items-end">
          <b-col cols="12 text-right mt-3">
            <b-button v-if="basicForm.status === 'active'" @click="onAction('inactivate',basicForm)" size="sm"
                      variant="warning default">
              <i class="icofont-ban"></i> {{$t('system-setting.status-inactive')}}
            </b-button>
            <b-button v-if="basicForm.status === 'inactive'" @click="onAction('activate',basicForm)" size="sm" variant="success default">
              <i class="icofont-check-circled"></i> {{$t('system-setting.status-active')}}
            </b-button>
            <b-button v-if="basicForm.status === 'inactive'" @click="onAction('delete',basicForm)" size="sm"
                      variant="danger default">
              <i class="icofont-bin"></i> {{$t('system-setting.delete')}}
            </b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
              class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
            </b-button>
          </b-col>
        </b-row>
        <div class="position-absolute" style="left: 3%;bottom: 10%">
          <img v-if="basicForm.status === 'inactive'" src="../../../assets/img/no_active_stamp.png">
          <img v-else-if="basicForm.status === 'active'" src="../../../assets/img/active_stamp.png">
        </div>
      </div>
    </b-card>

    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('inactive')" class="mr-1">
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
  import _ from 'lodash';
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {getApiManager} from '../../../api';
  import {validationMixin} from 'vuelidate';

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
        submitted:false,
        categoryData:[],
        pageStatus: 'list',
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'active', text: this.$t('permission-management.active')},
          {value: 'inactive', text: this.$t('permission-management.inactive')}
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
          archiveIndicatorsList:[],
          note:'',
          archivesTemplateId:0
        },
        categoryFilterData:[],
        categorySelectOptions: [],
        manufacturerOptions: [
          {text: "同方威视", value: "0"},
          {text: "海康威视", value: '1'},
          {text: "大华股份", value: '2'},
          {text: "华为", value: '3'}
        ],
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
                  "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
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

        deviceShowingTableItems: {
          apiUrl: apiBaseUrl + '/cakes/fordatatable',
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'list-item-heading'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('device-management.document-template.indicator'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '40%'
            },
            {
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.document-template.unit'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: '__slot:required',
              sortField: 'setting',
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
        tempData2: [
          {
            "no": 1,
            "number": "0000",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
          {
            "no": 2,
            "number": "0000",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
          {
            "no": 3,
            "number": "0000",
            "name": "首都机场",
            "status": false,
            "classify": "张三",
          },
          {
            "no": 4,
            "number": "0000",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
          {
            "no": 5,
            "number": "0000",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
          {
            "no": 6,
            "number": "0003",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
          {
            "no": 7,
            "number": "0005",
            "name": "首都机场",
            "status": true,
            "classify": "张三",
          },
        ],
      }
    },
    mounted() {
      this.getCategoryData();
      this.$refs.vuetable.$parent.transform = this.transformTemplateTable.bind(this);
    },
    methods: {
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
        this.filterOption =  {
          templateName: '',
            status: null,
            categoryId: null
        };
        this.$refs.vuetable.refresh();
      },
      onAction(value,data = null) {
        this.initialize(data);
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'edit':
            this.pageStatus = 'edit';
            break;
          case 'show':
            this.pageStatus = 'show';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'activate':
            this.updateItemStatus('active');
            break;
          case 'inactivate':
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
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
          temp.manufacturerName = getManufacturerName(this.manufacturerOptions,temp.manufacturer);
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
      vuetableHttpFetch(apiUrl,httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.filterOption
        });
      },
      initialize(data = null) {
        if (data == null)
          this.basicForm = {
            templateName: '',
            archivesTemplateNumber: '',
            categoryId: '',
            manufacturer: '',
            originalModel: '',
            archiveIndicatorsList:[],
            note:'',
            archivesTemplateId:0
          };
        else
          this.basicForm = data;
        this.submitted = false;
      },
      //save document template Item
      onSaveTemplate() {
        this.submitted = true;
        this.$v.basicForm.$touch();
        if (this.$v.basicForm.$invalid) {
          return;
        }
        let finalLink = this.basicForm.archivesTemplateId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/` + finalLink, {
            templateName: this.basicForm.templateName,
            archivesTemplateNumber: this.basicForm.archivesTemplateNumber,
            categoryId: this.basicForm.categoryId,
            manufacturer: this.basicForm.manufacturer,
            originalModel: this.basicForm.originalModel,
            archiveIndicatorsList:[],
            note:'',
            archivesTemplateId :  this.basicForm.archivesTemplateId
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
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-children`), {
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

      //todo for deviceShowingTable points.
      onDeviceShowingTablePaginationData(paginationData) {
        this.$refs.deviceShowingTablePagination.setPaginationData(paginationData)
      },
      onDeviceShowingTableChangePage(page) {
        this.$refs.deviceShowingTable.changePage(page)
      },
      deviceShowingTableDataManager(sortOrder, pagination) {
        let local = this.tempData2;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }
        pagination = this.$refs.deviceShowingTable.makePagination(
          local.length,
          this.deviceShowingTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.deviceShowingTableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
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
        this.categoryFilterData.push({value:null,text:`${this.$t('permission-management.all')}`})
      },
    }
  }
</script>
