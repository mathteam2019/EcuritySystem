<style>
  div.device-list-wrapper div.item {
    min-height: 200px;
    margin-bottom: 20px;
  }
</style>
<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('device-management.condition-monitoring')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" class="w-100" content-class="tab-content" :no-fade="true">

      <b-tab :title="$t('device-management.condition-monitoring')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4">

              <b-row>
                <b-col xs="12" md="10"  class="d-flex justify-content-between">
                  <div class="w-70">
                    <b-row>
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
                      <b-col>
                        <b-form-group :label="$t('device-management.device-name')">
                          <b-form-input></b-form-input>
                        </b-form-group>
                      </b-col>
                    </b-row>
                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.search') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.reset') }}</b-button>
                    <b-button v-if="isListMode" size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.export') }}</b-button>
                    <b-button v-if="isListMode" size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.print') }}</b-button>
                  </div>
                </b-col>
                <b-col xs="12" md="2" class="text-md-right">
                  <b-row>
                    <b-colxx xxs="12">
                      <label>{{$t('device-management.switch-image-list')}}</label>
                    </b-colxx>
                    <b-colxx xxs="12">
                      <switches
                        v-model="isListMode"
                        theme="custom"
                        color="primary-inverse"
                      ></switches>
                    </b-colxx>
                  </b-row>
                </b-col>
              </b-row>
              <b-row v-if="!isListMode" class="device-list-wrapper">
                <b-col xs="12" md="4" xxl="3">
                  <b-card class="item flex-row" no-body>
                    <div class="w-50 position-relative">
                      <img class="card-img-left" src="/assets/img/card-thumb-1.jpg" alt="Card cap" />
                    </div>
                    <div class="w-50">
                      <b-card-body>
                        <b-badge variant="success" pill class="position-absolute badge-top-right ">{{$t('device-management.status-ok')}}</b-badge>
                        <h2 class="mb-4 card-subtitle font-weight-bold">MW1000AA-001</h2>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.classify')}}: 人体查验设备</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.package')}}: 通道001</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.manufacture')}}：同方威视</p>
                        <p class="card-text text-small mb-0 font-weight-light">IP: 192.168.22.235</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.register-number')}}:</p>
                      </b-card-body>
                    </div>
                  </b-card>
                </b-col>
                <b-col xs="12" md="4" xxl="3">
                  <b-card class="item flex-row" no-body>
                    <div class="w-50 position-relative">
                      <img class="card-img-left" src="/assets/img/card-thumb-1.jpg" alt="Card cap" />
                    </div>
                    <div class="w-50">
                      <b-card-body>
                        <b-badge variant="danger" pill class="position-absolute badge-top-right">{{$t('device-management.status-bad')}}</b-badge>
                        <h2 class="mb-4 card-subtitle font-weight-bold">MW1000AA-001</h2>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.classify')}}: 人体查验设备</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.package')}}: 通道001</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.manufacture')}}：同方威视</p>
                        <p class="card-text text-small mb-0 font-weight-light">IP: 192.168.22.235</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.register-number')}}:</p>
                      </b-card-body>
                    </div>
                  </b-card>
                </b-col>
                <b-col xs="12" md="4" xxl="3">
                  <b-card class="item flex-row" no-body>
                    <div class="w-50 position-relative">
                      <img class="card-img-left" src="/assets/img/card-thumb-1.jpg" alt="Card cap" />
                    </div>
                    <div class="w-50">
                      <b-card-body>
                        <b-badge variant="light" pill class="position-absolute badge-top-right ">{{$t('device-management.status-unset')}}</b-badge>
                        <h2 class="mb-4 card-subtitle font-weight-bold">MW1000AA-001</h2>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.classify')}}: 人体查验设备</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.package')}}: 通道001</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.manufacture')}}：同方威视</p>
                        <p class="card-text text-small mb-0 font-weight-light">IP: 192.168.22.235</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.register-number')}}:</p>
                      </b-card-body>
                    </div>
                  </b-card>
                </b-col>
                <b-col xs="12" md="4" xxl="3">
                  <b-card class="item flex-row" no-body>
                    <div class="w-50 position-relative">
                      <img class="card-img-left" src="/assets/img/card-thumb-1.jpg" alt="Card cap" />
                    </div>
                    <div class="w-50">
                      <b-card-body>
                        <b-badge variant="success" pill class="position-absolute badge-top-right ">{{$t('device-management.status-ok')}}</b-badge>
                        <h2 class="mb-4 card-subtitle font-weight-bold">MW1000AA-001</h2>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.classify')}}: 人体查验设备</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.package')}}: 通道001</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.manufacture')}}：同方威视</p>
                        <p class="card-text text-small mb-0 font-weight-light">IP: 192.168.22.235</p>
                        <p class="card-text text-small mb-0 font-weight-light">{{$t('device-management.register-number')}}:</p>
                      </b-card-body>
                    </div>
                  </b-card>
                </b-col>
              </b-row>
              <b-row v-if="isListMode" class="">
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
                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>

            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('device-management.business-stat')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4" :title="$t('device-management.business-stat')">
              <h1>It will be soon!</h1>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>


  </div>
</template>
<script>

  import {apiUrl} from "../../../constants/config";
  import _ from 'lodash';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import Switches from 'vue-switches'
  import Vue from 'vue'


  export default {
    components: {
      'v-select': vSelect,
      'switches' : Switches,
      'vuetable' : Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap' : VuetablePaginationBootstrap
    },
    data() {
      return {
        isListMode:false,
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
              name: 'device-name',
              sortField: 'device-name',
              title: this.$t('device-management.device-name'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('system-setting.status'),
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
              name: 'passage',
              sortField: 'passage',
              title: this.$t('device-management.package'),
              titleClass: 'text-center',
              dataClass: 'text-center'
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
              name: 'origin_no',
              sortField: 'origin_no',
              title: this.$t('device-management.origin-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'ip',
              sortField: 'ip',
              title: 'IP',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'number',
              sortField: 'number',
              title: this.$t('device-management.number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "device-no": "A000",
            "device-name": "MW毫米波安检仪000",
            "status": "active",
            "passage": null,
            "classify": '单兵设备',
            "manufacturer": "张三",
            "origin_no": "13800001234",
            "ip": "192.168.3.14",
            "number": "MX10001"
          },
          {
            "no": 2,
            "device-no": "A001",
            "device-name": "MW毫米波安检仪000",
            "status": "inactive",
            "passage": null,
            "classify": '单兵设备',
            "manufacturer": "张三",
            "origin_no": "13800001234",
            "ip": "192.168.3.14",
            "number": "MX10002"
          },
          {
            "no": 3,
            "device-no": "A000",
            "device-name": "MW毫米波安检仪000",
            "status": "active",
            "passage": null,
            "classify": '单兵设备',
            "manufacturer": "张三",
            "origin_no": "13800001234",
            "ip": "192.168.3.14",
            "number": "MX10001"
          },
          {
            "no": 4,
            "device-no": "A000",
            "device-name": "MW毫米波安检仪000",
            "status": "active",
            "passage": null,
            "classify": '单兵设备',
            "manufacturer": "张三",
            "origin_no": "13800001234",
            "ip": "192.168.3.14",
            "number": "MX10001"
          },
          {
            "no": 5,
            "device-no": "A005",
            "device-name": "MW毫米波安检仪000",
            "status": "active",
            "passage": null,
            "classify": '监管查验设备/人体查验设备',
            "manufacturer": "同方威视",
            "origin_no": "13800001234",
            "ip": "192.168.3.14",
            "number": "MX10001"
          },
        ],
      }
    },
    methods: {
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
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
