<style lang="scss">
  .document-management {
      .v-select.v-select-custom-style {
        & > div {
          border-radius: 0.3rem !important;

          & > div {
            border-radius: 0.3rem !important;
          }
        }

      }
    .form-group {
      label.input-label {
        line-height: 2.25rem;
        /*height: 2.25rem;
        margin-bottom: 0;*/
      }
    }

    div.img-wrapper {
      width: 270px;
      height: 420px;
      padding: 30px;
      border: solid 1px #bdbaba;
      border-radius: 3px;
      position: relative;

      img {
        width: 100%;
        object-fit: scale-down;
      }
    }
  }
</style>
<template>
  <div class="document-management">
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
                <b-form-group :label="$t('device-management.template')">
                  <b-form-input v-model="filterOption.archivesName"/>
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
              <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_archive_export')">
              <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()" :disabled="checkPermItem('device_archive_print')">
              <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default" :disabled="checkPermItem('device_archive_create')">
              <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
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
                track-by="archiveId"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.archivesNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm"
                            variant="primary default btn-square"
                            :disabled="props.rowData.status === '1000000701' || checkPermItem('device_archive_modify')">
                    <i class="icofont-edit"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)" :disabled="checkPermItem('device_archive_update_status')"
                    variant="success default btn-square">
                    <i class="icofont-check-circled"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000701'"
                    size="sm" @click="onAction('inactivate',props.rowData)" :disabled="checkPermItem('device_archive_update_status')"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"/>
                  </b-button>
                  <b-button
                    size="sm" @click="onAction('delete',props.rowData)"
                    variant="danger default btn-square"
                    :disabled="props.rowData.status === '1000000701' || checkPermItem('device_archive_delete')">
                    <i class="icofont-bin"/>
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
              />
            </div>
          </b-col>
        </b-row>
      </div>
      <div v-show="pageStatus !== 'list'" class="form-section">
        <b-row class="h-100">
          <b-col cols="8">
            <b-row>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.file-no')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="archivesForm.archivesNumber" :state="!$v.archivesForm.archivesNumber.$dirty ? null : !$v.archivesForm.archivesNumber.$invalid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.archivesForm.archivesNumber.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.file-name')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="archivesForm.archivesName" :state="!$v.archivesForm.archivesName.$dirty ? null : !$v.archivesForm.archivesName.$invalid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.archivesForm.archivesName.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.template-name')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="archivesForm.archivesTemplateId" :options="templateOptions" :state="!$v.archivesForm.archivesTemplateId.$dirty ? null : !$v.archivesForm.archivesTemplateId.$invalid"
                                 :disabled="pageStatus === 'show'" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.archivesForm.archivesTemplateId.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                  </template>
                  <label class="input-label">{{this.templateForm.category}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.manufacture')">
                  <label class="input-label">{{this.templateForm.manufacturer}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.origin-model')">
                  <label class="input-label">{{this.templateForm.originalModel}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-5">
              <b-col cols="12" class="d-flex align-items-center">
                <label class="pr-2 m-0 "
                       style="color: #bdbaba">{{$t('device-management.archive.technical-indicator')}}</label>
                <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
              </b-col>
            </b-row>
            <b-form>
              <b-row>

                <b-col cols="4" v-for="(item,index) in indicatorsData">
                  <b-form-group>
                    <template slot="label">{{item.indicatorsName}}
                      <span v-if="item.isNull ==='1000000601'" class="text-danger">*</span>
                      <span class="font-weight-normal text-dark" v-if="item.indicatorsUnit!==null">( {{item.indicatorsUnit}} )</span>
                    </template>
                    <b-form-input v-model="indicatorsForm[index]"/>
                    <div class="invalid-feedback d-block">
                      {{ (submitted &&invalidIndicators.includes(index))?
                      $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                    </div>
                  </b-form-group>
                </b-col>

              </b-row>
            </b-form>
          </b-col>
          <b-col cols="4" class="d-flex flex-column align-items-center">
            <div class="img-wrapper">
              <img v-if="archivesForm.image!=null&&archivesForm.image!==''" :src="archivesForm.image"/>
              <img v-else-if="!(archivesForm.image!=null&&archivesForm.image!=='')"
                   src="../../../assets/img/device.png">
              <div class="position-absolute" style="bottom: -18%;left: -41%">
                <img v-if="archivesForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                <img v-if="archivesForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
              </div>
            </div>
            <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
            <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12 d-flex align-items-end justify-content-end mt-3">
            <div>
              <b-button size="sm" v-if="pageStatus !== 'show'" @click="saveArchivesItem()" variant="info default"><i
                class="icofont-save"/>
                {{$t('device-management.save')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !== 'create' && archivesForm.status === '1000000702'"
                        @click="onAction('activate',archivesForm)" variant="success default" :disabled="checkPermItem('device_archive_update_status')">
                <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !== 'create' && archivesForm.status === '1000000701'"
                        @click="onAction('inactivate',archivesForm)" variant="warning default" :disabled="checkPermItem('device_archive_update_status')">
                <i class="icofont-ban"/> {{$t('system-setting.status-inactive')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !=='create' && archivesForm.status === '1000000702'" :disabled="checkPermItem('device_archive_delete')"
                        @click="onAction('delete',archivesForm)" variant="danger default"><i class="icofont-bin"></i>
                {{$t('device-management.delete')}}
              </b-button>
              <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </div>

    </b-card>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-management.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-management.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal  centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export') }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExport()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
     <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
  </div>
</template>

<script>
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {downLoadFileFromServer, getApiManager, printFileFromServer} from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDicDataByDicIdForOptions, getDirection} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

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
      'v-select': vSelect,
      Modal
    },
    mounted() {
      this.getCategoryData();
      this.getTemplateData();
      this.getManufacturerOptions();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
    },
    mixins: [validationMixin],
    validations: {
      fileSelection : {
        required
      },
      archivesForm: {
        archivesName: {
          required
        },
        archivesTemplateId: {
          required
        },
        archivesNumber: {
          required
        }
      }
    },
    data() {
      return {
        isLoading: false,
        submitted: false,
        templateData: [],
	link: '',
        params: {},
        name: '',
        fileSelection : [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
		isModalVisible: false,
        templateOptions: [],
        categoryData: [],
        categoryFilterData: [],
        categorySelectOptions: [],
        pageStatus: 'list',
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],

        manufacturerOptions: [],
        indicatorsData: [],
        filterOption: {
          archivesName: '',
          status: null,
          categoryId: null
        },
        templateForm: {
          category: '',
          manufacturer: '',
          originalModel: ''
        },
        indicatorsForm: {},
        invalidIndicators: [],
        archivesForm: {
          archiveId: 0,
          archivesTemplateId: null,
          archivesName: null,
          archivesNumber: null,
          imageUrl: null,
          image: null,
          note: '',
          status: '1000000702',
          archiveValueList:[]
        },

        vuetableItems: {
          apiUrl: `${apiBaseUrl}/device-management/document-management/archive/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archiveId',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'archivesNumber',
              title: this.$t('device-management.file-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archivesName',
              title: this.$t('device-management.device-list.template'),
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
              name: 'categoryName',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturerName',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'originalModelName',
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
        deviceForm: {
          method: null,
        },
        extraForm: {
          method: null,
        },
      }
    },
    methods: {
      ///////////////////////////////////////////
      ////////   loading      Options ///////////
      ///////////////////////////////////////////
      // showModal() {
      //   let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
      //   let checkedIds = this.$refs.taskVuetable.selectedTo;
      //   this.params = {
      //     'isAll': checkedIds.length > 0 ? checkedAll : true,
      //     'filter': this.filter,
      //     'idList': checkedIds.join()
      //   };
      //   this.link = `task/invalid-task/generate`;
      //   this.name = 'Invalid-Task';
      //   this.isModalVisible = true;
      // },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManufacturerOptions(){
        this.manufacturerOptions =  getDicDataByDicIdForOptions(9);
      },
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        this.link = `device-management/document-management/archive`;
        this.name = 'document';
        this.isModalVisible = true;
      },
      onExport(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-management/archive`;
        if(this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'document', this.fileSelection);
          this.hideModal('model-export')
        }
      },
      onPrintButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-management/archive`;
        printFileFromServer(link,params);
      },
      hideModal(modal) {
        this.$refs[modal].hide();
      },
      //get device category data
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
      //get document template list
      getTemplateData() {
        getApiManager().post(`${apiBaseUrl}/device-management/document-template/archive-template/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.templateData = data;
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
            this.updateItemStatus('1000000701');
            break;
          case 'inactivate':
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.$refs['modal-delete'].show();
            break;
        }
      },
      initialize(data = null) {
        if (data == null)
          this.archivesForm = {
            archiveId: 0,
            archivesTemplateId: null,
            archivesName: null,
            archivesNumber: null,
            image: null,
            imageUrl: null,
            note: '',
            status: '1000000702',
            archiveValueList:[]
          };
        else {
          if (Object.keys(data).includes('createdBy')) { //if getting data from table , needful to processing
            for (let key in this.archivesForm) {
              if (Object.keys(data).includes(key)) {
                if (key !== 'imageUrl' && key !== 'image')
                  this.archivesForm[key] = data[key];
                else if (key === 'imageUrl')
                  this.archivesForm.image = data['imageUrl'] ? apiBaseUrl + data['imageUrl'] : null;
              }
            }
          }
          else
            this.archivesForm = data;
        }
        this.submitted = false;
       // this.getTemplateDetailData(this.archivesForm.archivesTemplateId);
      },
      onFileChange(e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length)
          return;
        this.onCreateImage(files[0]);
      },
      onCreateImage(file) {
        this.archivesForm.image = new Image();
        let reader = new FileReader();
        reader.onload = (e) => {
          this.archivesForm.image = e.target.result;
        };
        reader.readAsDataURL(file);
        this.archivesForm.imageUrl = file;
      },
      getTemplateDetailData(templateId) {
        this.indicatorsData = [];
        this.templateForm = {
          category: '',
          manufacturer: '',
          originalModel: ''
        };

        for (let item of this.templateData) {
          if (item.archivesTemplateId === templateId) {
            this.templateForm.category = item.deviceCategory.categoryName;
            this.templateForm.manufacturer = getManufacturerName(this.manufacturerOptions,item.manufacturer);
            this.templateForm.originalModel = item.originalModel;
            this.indicatorsData = item.archiveIndicatorsList;
            break;
          }
        }
        let indicatorValues = [];
        if (this.archivesForm != null && Object.keys(this.archivesForm).includes('archiveValueList'))
          indicatorValues = this.archivesForm.archiveValueList;
        this.indicatorsForm = {};
        if (indicatorValues.length >0)
        {
          this.indicatorsData.forEach((item,index) => {
            this.indicatorsForm[index] = null;
            for (let v of indicatorValues){
              if(v.indicatorsId === item.indicatorsId){
                this.indicatorsForm[index] = v.value;
                break
              }
            }
          });
        }
        //todo indicator part...
      },
      //save archives Item
      saveArchivesItem() {

        this.submitted = true;
        this.$v.archivesForm.$touch();
        if (this.$v.archivesForm.$invalid) {
          return;
        }
        this.invalidIndicators = [];
        let isRequired = false;
        let indicateFormData = [];
        this.indicatorsData.forEach((item, index) => {
          if (Object.keys(this.indicatorsForm).includes(index + "") && (this.indicatorsForm[index]).trim()!== "") {
            indicateFormData.push({
              "indicatorsId": item.indicatorsId,
              "value": this.indicatorsForm[index]
            })
          } else if (item.isNull === '1000000601') {
            this.invalidIndicators.push(index);
            isRequired = true;
          }
        });
        if (isRequired) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-management.required-indicator-value`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        const formData = new FormData();
        for (let key in this.archivesForm) {
          if (key !== 'imageUrl' && key !== 'image' && key !=='archiveValueList')
            formData.append(key, this.archivesForm[key]);
          else if (key === 'imageUrl' && this.archivesForm['image'] !== null && this.archivesForm['imageUrl'] !== null)
            formData.append('imageUrl', this.archivesForm['imageUrl'], this.archivesForm['imageUrl'].name);
        }
        formData.append('json', JSON.stringify({"archiveValueList": indicateFormData}));
        this.isLoading = true;
        let finalLink = this.archivesForm.archiveId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-management/archive/` + finalLink, formData)
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-management.added-successfully`), {
                  duration: 10000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.vuetable.reload();
                this.isLoading = false;
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-management.has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-archive-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-archive-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-archive-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-archive-number`), {
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
        let archiveId = this.archivesForm.archiveId;
        if (archiveId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-management/archive/update-status`, {
            archiveId: archiveId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-management.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.archivesForm.archiveId > 0)
                  this.archivesForm.status = statusValue;
                if (this.pageStatus === 'list')
                  this.$refs.vuetable.reload();
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-management.has-devices`), {
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
      //remove archives
      removeItem() {
        let archiveId = this.archivesForm.archiveId;
        if (archiveId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-management/archive/delete`, {
            archiveId: archiveId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-management.deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.vuetable.refresh();
                if (this.archivesForm.archiveId > 0)
                  initialize();
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-management.has-devices`), {
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
      //methods relative showing list of archives
      transformTable(response) {
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
          temp.categoryName = temp.archiveTemplate ? temp.archiveTemplate.deviceCategory.categoryName : '';
          temp.manufacturerName = temp.archiveTemplate ? getManufacturerName(this.manufacturerOptions,temp.archiveTemplate.manufacturer) : '';
          temp.originalModelName = temp.archiveTemplate ? temp.archiveTemplate.originalModel : '';
          transformed.data.push(temp);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filterOption
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },

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
      templateData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.templateOptions = [];
        if (newVal.length === 0) {
          this.templateOptions.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          this.templateOptions = newVal.map(template => ({
            text: template.templateName,
            value: template.archivesTemplateId
          }));
        }

      },
      'archivesForm.archivesTemplateId': function (newVal,oldVal) {
          this.getTemplateDetailData(newVal);
      }
    }
  }
</script>

