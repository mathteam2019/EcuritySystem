<style lang="scss">
  .form-group {
    label.input-label {
      line-height: 36px;
    }
  }
  div.img-wrapper {
    width: 270px;
    height: 440px;
    padding: 30px;
    border: solid 1px #bdbaba;
    border-radius: 3px;
    img {
      width: 100%;
      object-fit: scale-down;
    }
  }
</style>
<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-card>
      <div v-if="pageStatus==='list'">
        <b-row>
          <b-col cols="6">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.file-name')">
                  <b-form-select :options="fileData" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select :options="deviceClassifyData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <div>
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
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            <vuetable
              ref="vuetable"
              :api-mode="false"
              :fields="vuetableItems.fields"
              :data-manager="dataManager"
              :per-page="vuetableItems.perPage"
              pagination-path="pagination"
              @vuetable:pagination-data="onPaginationData"
              class="table-striped"
            >
              <div slot="operating" slot-scope="props">
                <b-button @click="onAction('edit')"
                          size="sm"
                          variant="primary default btn-square"
                >
                  <i class="icofont-edit"></i>
                </b-button>
                <b-button
                  v-if="props.rowData.status=='inactive'"
                  size="sm"
                  variant="success default btn-square"
                >
                  <i class="icofont-check-circled"></i>
                </b-button>
                <b-button
                  v-if="props.rowData.status=='active'"
                  size="sm"
                  variant="warning default btn-square"
                >
                  <i class="icofont-ban"></i>
                </b-button>
                <b-button
                  size="sm"
                  variant="danger default btn-square"
                >
                  <i class="icofont-bin"></i>
                </b-button>
              </div>
            </vuetable>
            <vuetable-pagination-bootstrap
              ref="pagination"
              @vuetable-pagination:change-page="onChangePage"
            ></vuetable-pagination-bootstrap>
          </b-col>
        </b-row>
      </div>
      <div v-if="pageStatus==='create'" class="form-section">
        <b-row>
          <b-col cols="8">
            <b-row>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-no')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.number"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-name')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.name"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-list.archive')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="mainForm.templateId" :options="templateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.deviceClassify"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-classify')">
                  <label class="input-label">同方威视</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.origin-model')">
                  <label class="input-label">MW1000AA</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-3">
              <b-col cols="12" class="d-flex align-items-center">
                <label class="pr-2 m-0 "
                       style="color: #bdbaba">{{$t('device-management.device-list.device-information')}}</label>
                <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
              </b-col>
            </b-row>
            <b-row v-if="mainForm.templateId==='waveSecurityDevice'">
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.original-number')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.production-date')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.purchase-date')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier')">
                  <b-form-select v-model="deviceForm.supplier" :options="supplierOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.ip')">
                  <b-form-input v-model="deviceForm.method"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row v-if="mainForm.templateId!=='waveSecurityDevice'">
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
              <img :src="mainForm.image" alt="img" />
            </div>
            <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
            <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12 text-right mt-3">
            <b-button size="sm" variant="info default"><i class="icofont-save"></i> {{$t('device-management.save')}}</b-button>
            <b-button size="sm" variant="success default"><i class="icofont-check-circled"></i> {{$t('device-management.active')}}</b-button>
            <b-button size="sm" variant="danger default"><i class="icofont-bin"></i> {{$t('device-management.delete')}}</b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i class="icofont-arrow-left"></i> {{$t('device-management.return')}}</b-button>
          </b-col>
        </b-row>
      </div>
    </b-card>

  </div>
</template>

<script>
  import _ from 'lodash';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        pageStatus:'list',
        fileData: [
          '档案-1',
          '档案-2',
          '档案-3'
        ],
        deviceClassifyData: [
          '全部',
          '监管查验设备',
          '单兵设备',
        ],
        selectedStatus: 'all',
        vuetableItems: {
          perPage: 5,
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'device-no',
              sortField: 'device-no',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archive',
              sortField: 'archive',
              title: this.$t('device-management.device-list.archive'),
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
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturer',
              sortField: 'manufacturer',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'origin-no',
              sortField: 'origin-no',
              title: this.$t('device-management.origin-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center btn-actions'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "MW毫米波安检仪",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "华为M6平板",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 5,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 6,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 7,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
        ],
        stateOptions: [
          {value: null, text: this.$t('system-setting.status-all')},
          {value: "active", text: this.$t('system-setting.status-active')},
          {value: "inactive", text: this.$t('system-setting.status-inactive')},
        ],
        mainForm: {
          number: null,
          name: null,
          image:null,
          templateId: 'waveSecurityDevice',
        },
        deviceForm: {
          method: null,
          supplier:null
        },
        extraForm: {
          method: null,
        },
        templateOptions: [
          {value: "waveSecurityDevice", text: this.$t('device-management.device-list.wave-security-device')},
          {value: "mobileTablet", text: this.$t('device-management.device-list.huawei-m6-tablet')},
        ],
        supplierOptions:[
          '同方威视代理',
          '华为产品部'
        ],
      }
    },
    methods: {

      onSearchButton(){

      },
      onResetButton(){

      },
      onAction(value) {
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
        }
      },
      onFileChange(e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length)
          return;
        this.onCreateImage(files[0]);
      },
      onCreateImage(file) {
        this.mainForm.image = new Image();
        let reader = new FileReader();
        reader.onload = (e) => {
          this.mainForm.image = e.target.result;
        };
        reader.readAsDataURL(file);
        this.mainForm.portrait = file;
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },
      //todo need to remove with temp data
      dataManager(sortOrder, pagination) {
        let local = this.tempData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }

        pagination = this.$refs.vuetable.makePagination(
          local.length,
          this.vuetableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.vuetableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      }
    }
  }
</script>

