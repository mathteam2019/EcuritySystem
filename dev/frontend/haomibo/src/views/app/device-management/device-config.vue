<style lang="scss">
  .device-config {
    @import '../../../assets/css/dual-list.css';

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
    .card-section {
      border-radius: 0.3rem;
      background: #ededed;
      padding: 1rem!important;
      .input-group {
        .form-control {
          height: 38px;
        }
        .input-group-append {
          height: 38px;
          &>button {
            line-height: 1;
          }
        }
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
        <div class="switch-button d-flex">
          <span class="active"><i class="icofont-gear"></i></span><span><i class="icofont-listine-dots"></i></span>
        </div>
        <b-row>
          <b-col xs="12" md="4">
            <div class="card-section">
              <div>
                <label class=""></label>
                <b-input-group class="mb-3">
                  <b-form-input/>
                  <b-input-group-append>
                    <b-button variant="info default"><i class="icofont-search"></i> {{ $t('device-management.search') }}</b-button>
                  </b-input-group-append>
                </b-input-group>
                <div>
                  <tree
                    :filter="treeFilter0"
                    :data="treeData"
                    :options="treeOptions"
                    @node:selected="onNodeSelected"
                  />
                </div>
              </div>
            </div>
          </b-col>
          <b-col xs="12" md="8">
            <b-row>
              <b-colxx cols="12" id="dual_list_wrapper">
                <vue-dual-list :options="options"></vue-dual-list>
              </b-colxx>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('device-management.maintenance-config')">
        <b-row>
          <b-col cols="12" class="d-flex justify-content-between">
            <div class="w-70">
              <b-row>
                <b-col>
                  <b-form-group :label="$t('device-management.device-name')">
                    <b-form-input></b-form-input>
                  </b-form-group>
                </b-col>
                <b-col>
                  <b-form-group :label="$t('device-management.package')">
                    <v-select v-model="package" :options="packageData" :dir="direction"/>
                  </b-form-group>
                </b-col>
                <b-col>
                  <b-form-group :label="$t('device-management.device-classify')">
                    <v-select v-model="deviceClassify" :options="deviceClassifyData"
                              :dir="direction"/>
                  </b-form-group>
                </b-col>

              </b-row>
            </div>
            <div class="align-self-center">
              <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.search') }}</b-button>
              <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.reset') }}</b-button>
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="12">
            <b-card class="w-100 mb-1">
              <b-row>
                <b-col cols="10">
                  <b-row>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.device-name')}}</label>
                        <label class="col-8">A0001</label>
                      </div>
                    </b-col>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.device-name')}}</label>
                        <label class="col-8">A0001</label>
                      </div>
                    </b-col>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.device-name')}}</label>
                        <label class="col-8">A0001</label>
                      </div>
                    </b-col>
                  </b-row>
                  <b-row>
                    <b-col cols="12" sm="10" md="8" lg="6" class="mt-2">
                      <b-form-group :label="$t('device-management.connected-device')">
                        <v-select v-model="connectedDevice" multiple :options="connectedDeviceData" :dir="direction"/>
                      </b-form-group>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col cols="2">
                  <div class="d-flex flex-column justify-content-around h-100">
                    <b-button size="sm" class="ml-2" variant="danger">{{ $t('device-management.reset') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="success">{{ $t('device-management.save') }}</b-button>
                  </div>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from 'vuetable-2/src/components/Vuetable'
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
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'VueDualList': VueDualList
    },
    data() {
      return {
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
          buttonOption: {
            textLeft: this.$t('device-management.move_left'),
            textRight: this.$t('device-management.move_right')
          },
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

            {'id': '10', 'name': 'Serge'},
            {'id': '11', 'name': 'Kid'},
            {'id': '12', 'name': 'Lynx'},
            {'id': '13', 'name': 'Harle'},

          ],
          colorItems: '#1E90FF',
          selectedItems: []
        },
        treeFilter0: '',
        treeData: this.getData(),
        treeOptions: {direction: getDirection().direction}

      }
    },
    methods: {
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
      }
    }
  }
</script>
