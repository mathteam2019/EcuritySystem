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
      height: calc(100% - 54px);
      &.list {
        margin-top: -23px!important;
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
          <span :class="`${switchStatus==='config'?'active':''}`" @click="changeSwitchStatus('config')"><i class="icofont-gear"></i></span>
          <span :class="`${switchStatus==='list'?'active':''}`" @click="changeSwitchStatus('list')"><i class="icofont-listine-dots"></i></span>
        </div>
        <b-row v-if="switchStatus==='config'" class="second-row" >
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
                <b-col cols="12" class="d-flex justify-content-end align-self-end">
                  <b-button size="sm" variant="info default mr-1">
                    <i class="icofont-save"></i>
                    {{ $t('permission-management.save-button') }}
                  </b-button>
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>

        <b-row v-if="switchStatus==='list'" class="second-row list">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('device-management.device-name')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                      <b-form-select v-model="filterForm.deviceClassification" :options="deviceClassificationOptions"
                                     plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="filterForm.position" :options="positionOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default">
                  <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default">
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
                    :api-mode="false"
                    :fields="configListTableItems.fields"
                    :data-manager="configListTableDataManager"
                    :per-page="configListTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onConfigTablePaginationData"
                    class="table-striped"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary">{{ props.rowData.number}}</span>
                    </div>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="configListTablePagination"
                    :initial-per-page="configListTableItems.perPage"
                    @vuetable-pagination:change-page="onConfigTableChangePage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('device-management.maintenance-config')">
        <b-row v-if="pageStatus ==='list'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device')">
                      <b-form-input v-model="filterForm.device"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                      <b-form-select v-model="filterForm.deviceClassification" :options="deviceClassificationOptions"
                                     plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="filterForm.position" :options="positionOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                  <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                  <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="pendingListTable"
                    :api-mode="false"
                    :fields="pendingListTableItems.fields"
                    :data-manager="pendingListTableDataManager"
                    :per-page="pendingListTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onBlackListTablePaginationData"
                    class="table table-striped"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary"
                        @click="onAction('show')">{{ props.rowData.number}}</span>
                    </div>
                    <div slot="operating" slot-scope="props">
                      <b-button size="sm" variant="info default btn-square"
                                @click="onAction('show')">
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
                    @vuetable-pagination:change-page="onBlackListTableChangePage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus === 'show'" class="h-100 form-section">
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
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.atr-insuspicion-process')}}
                  </template>
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.safety-hand-check')}}
                  </template>
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-guide-object')}}
                  </template>
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}
                  </template>
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.monitor-group')}}
                  </template>
                  <b-form-select :options="[]" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-inspection-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain />
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.hand-check-position')}}
                  </template>
                  <b-form-input />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-inspection-object')}}
                  </template>
                  <b-form-select :options="genderFilterOptions" plain />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}&nbsp;
                  </template>
                  <b-form-select :options="genderFilterOptions" plain />
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="6">
                <b-form-group class="full-width">
                  <template slot="label">
                    {{$t('device-config.maintenance-config.suitable-for')}}&nbsp;
                  </template>
                  <b-form-select :options="[]" plain />
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
              <b-button @click="onAction('list')" variant="info default" size="sm"><i class="icofont-long-arrow-left"></i> {{
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
  import _ from 'lodash';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import Switches from 'vue-switches'
  import Vue from 'vue'
  import VueDualList from '../../../components/Duallist/VueDualList'
  import LiquorTree from 'liquor-tree'

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
    data() {
      return {
        pageStatus: 'list',
        deviceClassificationOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'classification-1', text: '分类1'},
          {value: 'classification-2', text: '分类2'},
          {value: 'classification-3', text: '分类3'},
        ],
        positionOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'position-1', text: '清理'},
          {value: 'position-2', text: '维修'}
        ],
        filterForm: {
          task: null,
          status: null,
          position: null,
          deviceClassification: null,
          device: null
        },
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
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('maintenance-management.maintenance-task.number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'device',
              sortField: 'device',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'position',
              sortField: 'position',
              title: this.$t('maintenance-management.maintenance-task.position'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'applied',
              sortField: 'applied',
              title: this.$t('device-config.maintenance-config.applied'),
              titleClass: 'text-center',
              dataClass: 'text-center'
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
        tempData: [
          {
            "no": 1,
            "number": "H201909200001",
          },
          {
            "no": 2,
            "number": "H201909200002",
          },
          {
            "no": 3,
            "number": "H201909200003",
          },
          {
            "no": 4,
            "number": "H201909200004",
          },
        ],

        configListTableItems: {
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
                    title: this.$t('maintenance-management.maintenance-task.no'),
                    titleClass: 'text-center',
                    dataClass: 'text-center'
                },
                {
                    name: '__slot:number',
                    sortField: 'number',
                    title: this.$t('device-management.device-no'),
                    titleClass: 'text-center',
                    dataClass: 'text-center'
                },
                {
                    name: 'device',
                    sortField: 'device',
                    title: this.$t('maintenance-management.maintenance-task.device'),
                    titleClass: 'text-center',
                    dataClass: 'text-center'
                },
                {
                    name: 'device-classification',
                    sortField: 'position',
                    title: this.$t('menu.device-classify'),
                    titleClass: 'text-center',
                    dataClass: 'text-center'
                },
                {
                    name: 'site',
                    sortField: 'applied',
                    title: this.$t('device-management.site'),
                    titleClass: 'text-center',
                    dataClass: 'text-center'
                },
            ]
        },
        configTempData: [
            {
                "no": 1,
                "number": "0000",
                "device": "平板32",
                "site": "首都机场/1号航站楼"
            },
            {
                "no": 2,
                "number": "0100",
                "device": "摄像头12",
                "site": "首都机场/1号航站楼"
            },
            {
                "no": 3,
                "number": "0200",
                "device": "安检仪01",
                "site": "首都机场/2号航站楼/通道2"
            },
            {
                "no": 4,
                "number": "0300",
                "device": "平板021",
                "site": "首都机场/2号航站楼/通道2"
            },
        ],
        switchStatus: 'config', // config / list

        genderFilterOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'male', text: this.$t('permission-management.male')},
          {value: 'female', text: this.$t('permission-management.female')},
          {value: 'other', text: this.$t('permission-management.unknown')},
        ]
      }
    },
    methods: {
      onSearchButton() {
      },
      onResetButton() {
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
      pendingListTableDataManager(sortOrder, pagination) {
        let local = this.tempData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }

        pagination = this.$refs.pendingListTable.makePagination(
          local.length,
          this.pendingListTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.pendingListTableItems.perPage;
        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      },
      onBlackListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
      },
      onBlackListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
      },

      changeSwitchStatus(status) {
        this.switchStatus = status;
      },

      configListTableDataManager(sortOrder, pagination) {
          let local = this.configTempData;

          // sortOrder can be empty, so we have to check for that as well
          if (sortOrder.length > 0) {
              local = _.orderBy(
                  local,
                  sortOrder[0].sortField,
                  sortOrder[0].direction
              );
          }

          pagination = this.$refs.pendingListTable.makePagination(
              local.length,
              this.configListTableItems.perPage
          );

          let from = pagination.from - 1;
          let to = from + this.configListTableItems.perPage;
          return {
              pagination: pagination,
              data: _.slice(local, from, to)
          };
      },
      onConfigTablePaginationData(paginationData) {
          this.$refs.configListTablePagination.setPaginationData(paginationData);
      },
      onConfigTableChangePage(page) {
          this.$refs.configListTable.changePage(page);
      },
    }
  }
</script>
