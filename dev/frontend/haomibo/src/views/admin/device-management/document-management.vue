<style lang="scss">
  .document-management {
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
    <b-card class="main-without-tab">
      <div v-if="pageStatus=='list'" class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.template')">
                  <b-form-input v-model="filterOption.archivesName"></b-form-input>
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
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.archivesNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm"
                            variant="primary default btn-square"
                            :disabled="props.rowData.status === 'active'">
                    <i class="icofont-edit"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='inactive'"
                    size="sm" @click="onAction('activate',props.rowData)"
                    variant="success default btn-square">
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='active'"
                    size="sm" @click="onAction('inactivate',props.rowData)"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"></i>
                  </b-button>
                  <b-button
                    size="sm" @click="onAction('delete',props.rowData)"
                    variant="danger default btn-square"
                    :disabled="props.rowData.status === 'active'">
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
      <div v-if="pageStatus !== 'list'" class="form-section">
        <b-row class="h-100">
          <b-col cols="8">
            <b-row>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.file-no')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="archivesForm.archivesNumber"></b-form-input>
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
                  <b-form-input v-model="archivesForm.archivesName"></b-form-input>
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
                  <b-form-select v-model="archivesForm.archivesTemplateId" :options="templateOptions" plain/>
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
            <b-row v-if="archivesForm.templateId==='waveSecurityDevice'">
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.inspection-method')">
                  <b-form-input v-model="deviceForm.inspectionMethod"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.single-scan-time')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.number-of-operator')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.detectable-item-type')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.automatic-identification')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.privacy-protection')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.equipment-size')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.channel-size')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.equipment-weight')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.power-by')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.rated-power')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.operating-temperature-humidity')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.storage-temperature-humidity')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row v-if="archivesForm.templateId!=='waveSecurityDevice'">
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.battery-capacity')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.running-memory')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.screen-size')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.storage-capacity')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.front-camera')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.rear-camera')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.scalable-capacity')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.operating-system')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.cpu-model')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.dimension')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.body-weight')">
                  <b-form-input v-model="extraForm.method"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex flex-column align-items-center">
            <div class="img-wrapper">
              <img v-if="archivesForm.image!=null&&archivesForm.image!==''" :src="archivesForm.image"/>
              <img v-else-if="!(archivesForm.image!=null&&archivesForm.image!=='')"
                   src="../../../assets/img/device.png">
              <div class="position-absolute" style="bottom: -18%;left: -41%">
                <img v-if="archivesForm.status === 'active'" src="../../../assets/img/active_stamp.png">
                <img v-if="archivesForm.status === 'inactive'" src="../../../assets/img/no_active_stamp.png">
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
                class="icofont-save"></i>
                {{$t('device-management.save')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !== 'create' && archivesForm.status === 'inactive'"
                        @click="onAction('activate',archivesForm)" variant="success default">
                <i class="icofont-check-circled"></i> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !== 'create' && archivesForm.status === 'active'"
                        @click="onAction('inactivate',archivesForm)" variant="warning default">
                <i class="icofont-ban"></i> {{$t('system-setting.status-inactive')}}
              </b-button>
              <b-button size="sm" v-if="pageStatus !=='create' && archivesForm.status === 'inactive'"
                        @click="onAction('delete',archivesForm)" variant="danger default"><i class="icofont-bin"></i>
                {{$t('device-management.delete')}}
              </b-button>
              <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </div>

    </b-card>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-management.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('inactive')" class="mr-1">
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
  </div>
</template>

<script>
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {getApiManager} from '../../../api';
  import {validationMixin} from 'vuelidate';

  const {required} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted() {
      this.getCategoryData();
      this.getTemplateData();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
    },
    mixins: [validationMixin],
    validations: {
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
        submitted: false,
        templateData: [],
        templateOptions: [],
        categoryData: [],
        categoryFilterData: [],
        categorySelectOptions: [],
        pageStatus: 'list',
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'active', text: this.$t('permission-management.active')},
          {value: 'inactive', text: this.$t('permission-management.inactive')}
        ],
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
        archivesForm: {
          archiveId: 0,
          archivesTemplateId: null,
          archivesName: null,
          archivesNumber: null,
          imageUrl: null,
          image: null,
          note: '',
          status: 'inactive'

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
              sortField: 'archiveId',
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
              sortField: 'archivesName',
              title: this.$t('device-management.device-list.template'),
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
        deviceForm: {
          method: null,
        },
        extraForm: {
          method: null,
        },
      }
    },
    methods: {
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
        this.$refs.vuetable.refresh();
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
            status: 'inactive'
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
        this.getTemplateDetailData(this.archivesForm.archivesTemplateId);
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
        this.templateForm = {
          category: '',
          manufacturer: '',
          originalModel: ''
        };
        for (let item of this.templateData) {
          if (item.archivesTemplateId === templateId) {
            this.templateForm.category = item.deviceCategory.categoryName;
            this.templateForm.manufacturer = item.manufacturer;
            this.templateForm.originalModel = item.originalModel;
            break;
          }
        }
      },
      //save archives Item
      saveArchivesItem() {
        this.submitted = true;
        this.$v.archivesForm.$touch();
        if (this.$v.archivesForm.$invalid) {
          return;
        }
        const formData = new FormData();
        for (let key in this.archivesForm) {
          if (key !== 'imageUrl' && key !== 'image')
            formData.append(key, this.archivesForm[key]);
          else if (key === 'imageUrl' && this.archivesForm['image'] !== null)
            formData.append(key, this.archivesForm[key], this.archivesForm[key].name);
        }
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
                break;
            }
          })
          .catch((error) => {
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
                  this.$refs.vuetable.refresh();
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
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-management.has-children`), {
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
          temp.manufacturerName = temp.archiveTemplate ? temp.archiveTemplate.manufacturer : '';
          temp.originalModelName = temp.archiveTemplate ? temp.archiveTemplate.originalModel : '';
          transformed.data.push(temp);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
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
      'archivesForm.archivesTemplateId': function (newVal) {
        this.getTemplateDetailData(newVal);
      }
    }
  }
</script>

