<style lang="scss">
  .device-classify {
    .form-section {
      height: 100%;
    }
  }
</style>
<template>
  <div class="device-classify">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div v-if="pageStatus=='list'" class="h-100 d-flex flex-column">
        <b-row class="pt-4">
          <b-col cols="6">
            <b-row>
              <b-col >
                <b-form-group :label="$t('device-management.device-classify-item.classify')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>

              <b-col >
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select :options="stateOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col >
                <b-form-group :label="$t('device-management.device-classify-item.super-classify')">
                  <b-form-input></b-form-input>
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

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="deviceClassifyTable"
                :api-mode="false"
                :fields="deviceClassifyTableItems.fields"
                :data-manager="dataManager"
                :per-page="deviceClassifyTableItems.perPage"
                pagination-path="pagination"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('edit')">{{ props.rowData.number }}</span>
                </div>
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
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>

      </div>
      <div v-if="pageStatus=='create'" class="h-100 d-flex flex-column">
        <b-row class="form-section d-flex">
          <b-col cols="6">
            <b-row>
              <b-col cols="6" >
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device-number')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="classifyForm.number"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6" >
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.device')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="classifyForm.orgId" :options="orgSelectData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device-number')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="classifyForm.parentNumber"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.parent-device')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="classifyForm.parentOrgId" :options="parentOrgSelectData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify-item.remark')}}
                  </template>
                  <b-form-textarea :rows="3" v-model="classifyForm.note"></b-form-textarea>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12 text-right mt-3 " class="align-self-end">
            <b-button size="sm" variant="info default"><i class="icofont-save"></i> {{$t('device-management.save')}}</b-button>
            <b-button size="sm" variant="success default"><i class="icofont-check-circled"></i> {{$t('device-management.active')}}</b-button>
            <b-button size="sm" variant="danger default"><i class="icofont-bin"></i> {{$t('device-management.delete')}}</b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}</b-button>
          </b-col>
          <div class="position-absolute" style="left: 3%;bottom: 17%">
            <img src="../../../assets/img/no_active_stamp.png">
          </div>
        </b-row>
      </div>
    </b-card>
  </div>
</template>

<script>
  import _ from 'lodash';
  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {getApiManager} from '../../../api';

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted(){
      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
        type: 'with_parent'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgData = data;
            break;
        }
      });
    },
    data () {
      return {
        orgData:[],
        orgSelectData:[],
        parentOrgSelectData:[],
        pageStatus:'list',
        selectedStatus: 'all',
        classifyForm:{
         number:null,
         orgId:null,
         parentNumber:null,
         parentOrgId:null,
         note:null
        },
        deviceClassifyTableItems: {
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'site-no',
              title: this.$t('device-management.device-classify-item.device-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'site-name',
              sortField: 'site-name',
              title: this.$t('device-management.device-classify-item.classify'),
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
                if(!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'super-site-no',
              sortField: 'super-site-no',
              title: this.$t('device-management.device-classify-item.super-classify-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if(value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'super-site-name',
              sortField: 'super-site-name',
              title: this.$t('device-management.device-classify-item.super-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if(value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'remarks',
              sortField: 'remarks',
              title: this.$t('system-setting.remarks'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "number": "0000",
            "site-name": "首都机场",
            "status": "active",
            "super-site-no": null,
            "super-site-name": null,
            "manager": "张三",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "site-no": "0100",
            "site-name": "1号航站楼",
            "status": "active",
            "super-site-no": "0000",
            "super-site-name": "总部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "site-no": "0200",
            "site-name": "2号航站楼",
            "status": "active",
            "super-site-no": "0000",
            "super-site-name": "总部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "site-no": "0201",
            "site-name": "通道1",
            "status": "active",
            "super-site-no": "0200",
            "super-site-name": "生产部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 5,
            "site-no": "0202",
            "site-name": "通道2",
            "status": "inactive",
            "super-site-no": "0200",
            "super-site-name": "生产部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 6,
            "site-no": "0300",
            "site-name": "3号航站楼",
            "status": "active",
            "super-site-no": "0000",
            "super-site-name": "总部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
          {
            "no": 7,
            "site-no": "0301",
            "site-name": "通道001",
            "status": "inactive",
            "super-site-no": "0300",
            "super-site-name": "销售部",
            "manager": "",
            "contact-info": "13800001234",
            "remarks": "",
          },
        ],
        stateOptions: {
          'all':this.$t('system-setting.status-all'),
          'valid':this.$t('system-setting.status-active'),
          'invalid':this.$t('system-setting.status-inactive')
        },
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
          case 'edit':
            this.pageStatus = 'create';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
        }
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.deviceClassifyTable.changePage(page);
      },
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

        pagination = this.$refs.deviceClassifyTable.makePagination(
          local.length,
          this.deviceClassifyTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.deviceClassifyTableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      },
      showCreatePage() { // move to create page
        // reset models
        this.classifyForm = {
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        };
        // change page to create
        this.pageStatus = 'create';
      },
    },
    watch: {
      'deviceClassifyTableItems.perPage': function (newVal) {
        this.$refs.deviceClassifyTable.refresh();
      },
      orgData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].orgId == org.parentOrgId) {
                return newVal[i];
              }
            }
            return null;
          };

          let stepValue = org;
          let level = 0;
          while (getParent(stepValue) !== null) {
            stepValue = getParent(stepValue);
            level++;
          }
          return level;
        };

        let generateSpace = (count) => {
          let string = '';
          while (count--) {
            string += '&nbsp;&nbsp;&nbsp;&nbsp;';
          }
          return string;
        };

        let selectOptions = [];

        newVal.forEach((org) => {
          selectOptions.push({
            value: org.orgId,
            html: `${generateSpace(getLevel(org))}${org.orgName}`
          });
        });
        this.orgSelectData = selectOptions;
        this.parentOrgSelectData = JSON.parse(JSON.stringify(selectOptions));
        this.parentOrgSelectData.push({value:null,html:`${this.$t('device-management.device-classify-item.no-classify')}`});
      },
    }
  }
</script>

