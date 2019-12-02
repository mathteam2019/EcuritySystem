<style lang="scss">
  @import '../../../assets/css/dual-list.css';

  .device-config {

    $cyan-button-color: #178af7;

    div.label-center label {
      display: flex;
      align-self: center;
    }
    .switch-button {
      justify-content: flex-end;
      padding-right: 1rem;
      span {
        padding: 5px;
        border: solid 1px #cccccc;
        background: #ededed;
        height: 32px;
        width: 32px;
        text-align: center;
        cursor: pointer;
        &:first-child {
          border-right-color: transparent !important;
        }
        &:last-child {
          border-left-color: transparent;
        }
        i {
          font-size: 16px;
        }
        &.active {
          border-color: #3182eb !important;
          background: #3182eb;
          color: white;
        }
      }
    }
    .second-row {
      height: calc(100% - 4rem);
      &.list {
        margin-top: -23px !important;
        height: calc(100% - 29px);
      }
    }

    .search-box {
      div[role='group'] {
        display: flex;
      }

      .form-control {
        max-width: calc(100% - 75px);
        border-top-right-radius: 0px;
        border-bottom-right-radius: 0px;
      }

      .btn {
        border-top-left-radius: 0px;
        border-bottom-left-radius: 0px;
      }
    }

    .btn.btn-cyan {
      background-color: $cyan-button-color;
      color: white;

      &:hover {
        background-color: $cyan-button-color;
      }
    }

    .btn.btn-outline-cyan {
      border-color: #cbcbcb;
      background-color: #ededed;
    }

    .section {
      background-color: #f4f4f4;
      padding: 1rem 0 0.5rem 0 !important;
    }

    div.label-center label {
      display: flex;
      align-self: center;
    }

    .form-section .form-group.full-width {
      max-width: unset;
      .form-control {
        max-width: unset;
      }
    }

  }

</style>
<template>
  <div class="device-config">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-tabs nav-class="ml-2" :no-fade="true">
      <b-tab :title="$t('device-management.site-config')">
        <div class="switch-button d-flex mb-3">
          <span :class="`${switchStatus==='config'?'active':''}`" @click="changeSwitchStatus('config')"><i
            class="icofont-gear"></i></span>
          <span :class="`${switchStatus==='list'?'active':''}`" @click="changeSwitchStatus('list')"><i
            class="icofont-listine-dots"></i></span>
        </div>
        <b-row v-show="switchStatus==='config'" class="second-row">
          <b-col cols="4" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-colxx cols="12">

                  <label class="font-weight-bold mb-3">{{$t('device-management.site')}}</label>
                  <b-form-group class="search-box">
                    <b-form-input size="sm"></b-form-input>
                    <b-button size="sm" variant="info default">
                      <i class="icofont-search"></i>
                      {{$t('device-management.search')}}
                    </b-button>
                  </b-form-group>

                </b-colxx>
                <b-colxx cols="12">
                  <tree
                    :filter="treeFilter0"
                    :data="treeData"
                    :options="treeOptions"
                    @node:selected="onNodeSelected"
                  />
                </b-colxx>
              </b-row>
            </div>
          </b-col>
          <b-col cols="8" class="d-flex flex-column pl-0">
            <div class="section d-flex flex-column h-100">
              <b-row class="mx-4 flex-grow-1">
                <b-col>
                  <vue-dual-list class="h-100 pb-3" :options="options"></vue-dual-list>
                </b-col>
              </b-row>
              <b-row class="mx-4">
                <b-col cols="12" class="d-flex justify-content-end align-self-end"
                       style="margin-left: 1.5rem; margin-right: 1.5rem;">
                  <b-button size="sm" variant="info default mr-1">
                    <i class="icofont-save"></i>
                    {{ $t('permission-management.save-button') }}
                  </b-button>
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>

        <b-row v-show="switchStatus==='list'" class="second-row list">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('device-management.device-name')">
                      <b-form-input v-model="configFilter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                      <b-form-select v-model="configFilter.categoryId" :options="deviceCategoryOptions"
                                     plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="configFilter.fieldId" :options="siteSelectOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onConfigSearchButton()">
                  <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onConfigResetButton()">
                  <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default">
                  <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.print') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default"><i class="icofont-printer"></i>&nbsp;
                  {{ $t('permission-management.export') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="configListTable"
                    :fields="configListTableItems.fields"
                    :api-url="configListTableItems.apiUrl"
                    :http-fetch="configListTableHttpFetch"
                    :per-page="configListTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onConfigTablePaginationData"
                    class="table-striped"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="configListTablePagination"
                    :initial-per-page="configListTableItems.perPage"
                    @vuetable-pagination:change-page="onConfigTableChangePage"
                    @onUpdatePerPage="configListTableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('device-management.maintenance-config')">
        <b-row v-show="pageStatus ==='list'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('device-management.device-name')">
                      <b-form-input v-model="pendingFilter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                      <b-form-select v-model="pendingFilter.categoryId" :options="deviceCategoryOptions"
                                     plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="pendingFilter.fieldId" :options="siteSelectOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onPendingSearchButton()">
                  <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onPendingResetButton()">
                  <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="pendingListTable"
                    :fields="pendingListTableItems.fields"
                    :api-url="pendingListTableItems.apiUrl"
                    :http-fetch="pendingListTableHttpFetch"
                    :per-page="pendingListTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onPendingListTablePaginationData"
                    class="table table-striped"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary"
                            @click="onAction('show',props.rowData)">{{ props.rowData.deviceSerialNumber}}</span>
                    </div>
                    <div slot="operating" slot-scope="props">
                      <b-button size="sm" variant="info default btn-square"
                                @click="onAction('show',props.rowData)">
                        <i class="icofont-edit"></i>
                      </b-button>
                      <b-button size="sm" variant="success default btn-square">
                        <i class="icofont-refresh"></i>
                      </b-button>
                    </div>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pendingListTablePagination"
                    :initial-per-page="pendingListTableItems.perPage"
                    @vuetable-pagination:change-page="onPendingListTableChangePage"
                    @onUpdatePerPage="pendingListTableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus === 'show'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.position')}}
                  </template>
                  <label>首都机场/1号航站楼</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device-classification')}}
                  </template>
                  <label>监管查验设备 / 人体查验设备</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device')}}
                  </template>
                  <label>安检仪001</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.operate-mode')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.atr-insuspicion-process')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.safety-hand-check')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-guide-object')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.monitor-group')}}
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-inspection-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.hand-check-position')}}
                  </template>
                  <b-form-input/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-inspection-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}&nbsp;
                  </template>
                  <b-form-select :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="6">
                <b-form-group class="full-width">
                  <template slot="label">
                    {{$t('device-config.maintenance-config.suitable-for')}}&nbsp;
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button variant="info default" size="sm">
                <i class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
              </b-button>
              <b-button variant="danger default" size="sm">
                <i class="icofont-bin"></i> {{$t('permission-management.delete')}}
              </b-button>
              <b-button @click="onAction('list')" variant="info default" size="sm"><i
                class="icofont-long-arrow-left"></i> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import _ from 'lodash';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";

  import Switches from 'vue-switches'
  import Vue from 'vue'
  import VueDualList from '../../../components/Duallist/VueDualList'
  import LiquorTree from 'liquor-tree'

  let getSiteFullName = orgData => {
    let orgFullName = '';
    if (orgData == null)
      return orgFullName;
    while (orgData.parent != null) {
      orgFullName += '/' + orgData.fieldDesignation;
      orgData = orgData.parent;
    }
    orgFullName = orgData.fieldDesignation + orgFullName;
    return orgFullName;
  };

  Vue.use(LiquorTree);
  Vue.treeFilter0 = '';//redefined filter option.
  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'switches': Switches,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'VueDualList': VueDualList
    },
    mounted() {
      this.getCategoryData();
      this.getSiteData();
      this.$refs.configListTable.$parent.transform = this.transformConfigTable.bind(this);
      this.$refs.pendingListTable.$parent.transform = this.transformPendingTable.bind(this);
    },
    data() {
      return {
        siteData: [],
        categoryData: [],
        pageStatus: 'list',
        switchStatus: 'config', // config / list
        deviceCategoryOptions: [],
        siteSelectOptions: [],
        pendingFilter: {
          deviceName: null,
          categoryId: null,
          fieldId: null
        },
        configFilter: {
          deviceName: null,
          categoryId: null,
          fieldId: null
        },
        manufacturerOptions: [
          {text: "同方威视", value: "0"},
          {text: "海康威视", value: '1'},
          {text: "大华股份", value: '2'},
          {text: "华为", value: '3'}
        ],
        genderFilterOptions: [
          {value: 'all', text: this.$t('permission-management.all')},
          {value: 'male', text: this.$t('permission-management.male')},
          {value: 'female', text: this.$t('permission-management.female')},
          {value: 'other', text: this.$t('permission-management.unknown')},
        ],
        maintenanceContactInfo: '',
        maintenanceDepart: '',
        maintenanceStaff: '',
        maintenanceDepartData: [
          '通道001',
          '通道002',
          '通道12',
        ],
        maintenanceStaffData: [
          '职员1',
          '职员2',
          '职员3',
        ],
        package: '',
        deviceClassify: '',
        direction: getDirection().direction,
        packageData: [
          '全部',
          '通道001',
          '通道002',
          '通道12',
        ],
        deviceClassifyData: [
          '全部',
          '监管查验设备 / 人体查验设备',
          '监管查验设备 / 物品查验设备',
          '监管查验设备 / 车辆查验设备',
          '单兵设备',
          '音视频监控设备 / 视频监控设备',
          '音视频监控设备 / 音频监控设备',
        ],
        connectedDevice: '',
        connectedDeviceData: [
          '全部',
          '通道001',
          '通道002',
          '通道12',
        ],
        isRequired: false,
        options: {
          label: this.$t('device-management.filter'),
          inputOptions: {uppercase: false, isRequired: false},
          isLtr: getDirection().direction,
          resizeBox: "md",
          items: [
            {'id': '1', 'name': 'Alundra'},
            {'id': '2', 'name': 'Jess'},
            {'id': '3', 'name': 'Meia'},
            {'id': '4', 'name': 'Melzas'},
            {'id': '5', 'name': 'Septimus'},

            {'id': '6', 'name': 'Rudy Roughknight'},
            {'id': '7', 'name': 'Jack Van Burace'},
            {'id': '8', 'name': 'Hanpan'},
            {'id': '9', 'name': 'Cecilia Adlehyde'},
          ],
          colorItems: '#1E90FF',
          selectedItems: []
        },
        treeFilter0: '',
        treeData: this.getData(),
        treeOptions: {direction: getDirection().direction},
        pendingListTableItems: {
          apiUrl: `${apiBaseUrl}/device-management/device-config/config/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'configId',
              sortField: 'configId',
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%',
            },
            {
              name: '__slot:number',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '13%'
            },
            {
              name: 'deviceName',
              sortField: 'deviceName',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '17%'
            },
            {
              name: 'siteNameWithParent',
              title: this.$t('device-management.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'fromConfig',
              sortField: 'fromConfig',
              title: this.$t('system-setting.parameter-setting.configuration'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '17%',
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '150px'
            }
          ]
        },
        configListTableItems: {
          apiUrl: `${apiBaseUrl}/device-management/device-table/device/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'deviceId',
              sortField: 'deviceId',
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: 'deviceSerial',
              sortField: 'deviceSerial',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'deviceName',
              sortField: 'deviceName',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: 'deviceCategoryName',
              title: this.$t('menu.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '25%'
            },
            {
              name: 'siteNameWithParent',
              title: this.$t('device-management.site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ]
        },


      }
    },
    methods: {
      //getting all device category options
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
      //getting all site options
      getSiteData() {
        getApiManager().post(`${apiBaseUrl}/site-management/field/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        });
      },
      onConfigSearchButton() {
        this.$refs.configListTable.refresh();
      },
      onConfigResetButton() {
        this.configFilter = {
          deviceName: '',
          categoryId: null,
          fieldId: null
        };
        this.$refs.configListTable.refresh();
      },
      onPendingSearchButton() {
        this.$refs.pendingListTable.refresh();
      },
      onPendingResetButton() {
        this.pendingFilter = {
          deviceName: '',
          categoryId: null,
          fieldId: null
        };
        this.$refs.pendingListTable.refresh();
      },
      onAction(value) {
        switch (value) {
          case 'list':
            this.pageStatus = 'list';
            break;
          case 'show':
            this.pageStatus = 'show';
            break;
        }
      },
      getData() {
        return Promise.resolve([
          {
            text: 'Item 1',
            state: {expanded: false},
            children: [
              {
                text: 'Item 2.1', state: {expanded: false}, children: [
                  {text: 'Item 3.1'},
                  {text: 'Item 3.2'},
                  {text: 'Item 3.3'}
                ]
              },
              {text: 'Item 2.2'},
              {text: 'Item 2.3'}
            ]
          },
        ])
      },
      onNodeSelected(node) {
        // console.log(node.text)
      },
      transformPendingTable(response) {
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
          temp.deviceSerialNumber = temp.device ? temp.device.deviceSerial : '';
          temp.deviceName = temp.device ? temp.device.deviceName : '';
          temp.siteNameWithParent = getSiteFullName(temp.device ? temp.device.field : null);
          temp.fromConfig = temp.fromConfigIdList.length > 0 ? temp.fromConfigDeviceName: '';
          transformed.data.push(temp);
        }
        return transformed
      },
      pendingListTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.pendingListTableItems.perPage,
          filter: this.pendingFilter
        });
      },
      onPendingListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
      },
      onPendingListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
      },

      changeSwitchStatus(status) {
        this.switchStatus = status;
      },
      transformConfigTable(response) {
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
          temp.deviceCategoryName = temp.archive ? temp.archive.archiveTemplate.deviceCategory.categoryName : '';
          temp.siteNameWithParent = getSiteFullName(temp.field);
          transformed.data.push(temp);
        }
        return transformed
      },
      configListTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.configListTableItems.perPage,
          filter: this.configFilter
        });
      },
      onConfigTablePaginationData(paginationData) {
        this.$refs.configListTablePagination.setPaginationData(paginationData);
      },
      onConfigTableChangePage(page) {
        this.$refs.configListTable.changePage(page);
      },
    },
    watch: {
      'configListTableItems.perPage': function (newVal) {
        this.$refs.configListTable.refresh();
      },
      'pendingListTableItems.perPage': function (newVal) {
        console.log(newVal);
        this.$refs.pendingListTable.refresh();
      },
      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let options = [];
        if (newVal.length === 0) {
          options.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          options = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.deviceCategoryOptions = JSON.parse(JSON.stringify(options));
        this.deviceCategoryOptions.push({value: null, text: `${this.$t('permission-management.all')}`})
      },
      siteData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].fieldId == org.parentFieldId) {
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
        this.siteSelectOptions = [];
        this.siteSelectOptions = newVal.map(org => ({
          text: org.fieldDesignation,
          html: `${generateSpace(getLevel(org))}${org.fieldDesignation}`
        }));
        this.siteSelectOptions.push({
          value: null,
          html: `${this.$t('permission-management.all')}`
        });

      }
    }
  }
</script>
