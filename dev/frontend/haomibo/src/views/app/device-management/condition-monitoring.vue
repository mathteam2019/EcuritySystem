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
                  </div>
                </b-col>
                <b-col xs="12" md="2" class="text-md-right">
                  <b-row>
                    <b-colxx xxs="12">
                      <label>{{$t('device-management.switch-image-list')}}</label>
                    </b-colxx>
                    <b-colxx xxs="12">
                      <switches
                        v-model="swichType"
                        theme="custom"
                        color="primary-inverse"
                      ></switches>
                    </b-colxx>
                  </b-row>
                </b-col>
              </b-row>
              <b-row class="device-list-wrapper">
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
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import Switches from 'vue-switches'
  import Vue from 'vue'

  Vue.component('custom-action', {
    template: [
      '<div>',
      '<button class="ui red button" @click="itemAction(\'view-item\', rowData)"><i class="zoom icon"></i></button>',
      '<button class="ui blue button" @click="itemAction(\'edit-item\', rowData)"><i class="edit icon"></i></button>',
      '<button class="ui green button" @click="itemAction(\'delete-item\', rowData)"><i class="delete icon"></i></button>',
      '</div>'
    ].join(''),

  })

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'switches' : Switches,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        swichType:false,
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
      }
    },
    methods: {

    }
  }
</script>
