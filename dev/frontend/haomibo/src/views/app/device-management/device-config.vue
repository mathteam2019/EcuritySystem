<style>
  @import '../../../assets/css/dual-list.css';

  .search-sm > input {
    font-size: 1.1rem;
    padding-left: 1rem;
  }

  .search-sm:after {
    top: 6px;
    right: 8px;
  }
  div.label-center label {
    display: flex;
    align-self: center;
  }

</style>
<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.device-config')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>
    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('device-management.site-config')">
        <b-row>
          <b-col xs="12" md="4">
            <b-card class="mb-4 h-100" no-body>
              <b-card-body>
                <b-row>
                  <b-colxx cols="12">
                    <div class="search-sm d-inline-block float-md-left mr-1 align-top w-100">
                      <b-input v-model="treeFilter0" :placeholder="$t('menu.search')"/>
                    </div>

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
              </b-card-body>
            </b-card>
          </b-col>
          <b-col xs="12" md="8">
            <b-card class="mb-4 h-100" no-body>
              <b-card-body>
                <b-row>
                  <b-colxx cols="12" id="dual_list_wrapper">
                    <vue-dual-list :options="options"></vue-dual-list>
                  </b-colxx>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('device-management.running-config')">
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
      <b-tab :title="$t('device-management.device-connect')">
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
        <b-row class="label-center">
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
                        <label class="col-4">{{$t('device-management.device-classify')}}</label>
                        <label class="col-8">监管查验设备 / 人体查验设备</label>
                      </div>
                    </b-col>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.site')}}</label>
                        <label class="col-8">首都机场/1号航站楼</label>
                      </div>
                    </b-col>
                  </b-row>
                  <b-row class="pt-4">
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.maintenance-department')}}</label>
                        <div class="col-8">
                          <v-select v-model="maintenanceDepart" :options="maintenanceDepartData"></v-select>
                        </div>
                      </div>
                    </b-col>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.maintenance-staff')}}</label>
                        <div class="col-8">
                          <v-select v-model="maintenanceStaff" :options="maintenanceStaffData"
                                    :dir="direction"/>
                        </div>
                      </div>
                    </b-col>
                    <b-col cols="4">
                      <div class="row">
                        <label class="col-4">{{$t('device-management.maintenance-contact-info')}}</label>
                        <div class="col-8">
                          <b-form-input v-model="maintenanceContactInfo"></b-form-input>
                        </div>
                      </div>
                    </b-col>
                  </b-row>
                  <b-row class="pt-4">
                    <b-col cols="8">
                      <div class="row">
                        <label class="col-2">{{$t('device-management.connected-device')}}</label>
                        <div class="col-6">
                          <v-select v-model="connectedDevice" multiple :options="connectedDeviceData" :dir="direction"/>
                        </div>
                      </div>
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

  import {apiUrl} from "../../../constants/config";
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
          isLtr:getDirection().direction,
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
        treeOptions: {direction:getDirection().direction}

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
