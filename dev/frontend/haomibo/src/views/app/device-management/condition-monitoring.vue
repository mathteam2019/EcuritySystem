<style lang="scss">
  .device-monitoring {
    .item-wrapper {
      &>.item {
        background-color: red;
        position: relative;
        width: 100%;
        padding-top: 82.44%;
        max-width: 410px;
        max-height: 338px;
        margin-bottom: 30px;
        display: inline-block;
      }
    }

  }

</style>
<template>
  <div class="device-monitoring">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div  class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.package')">
                  <b-form-select v-model="package" :options="packageData" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="deviceClassify" :options="deviceClassifyData" plain />
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-name')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" >
              <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="3" v-for="item in items">
            <div class="item-container">
              <div class="item-wrapper" >
                <div class="item" >
                  text
                </div>
              </div>
            </div>
          </b-col>
          <b-col cols="3" v-for="item in items">
            <div class="item-container">
              <div class="item-wrapper" >
                <div class="item" >
                  text
                </div>
              </div>
            </div>
          </b-col>
        </b-row>

      </div>
    </b-card>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import _ from 'lodash';
  import {getDirection} from "../../../utils";
  import Vue from 'vue'


  export default {
    components: {

    },
    data() {
      return {
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
        items: [
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
        ],
      }
    },
    methods: {

    }
  }
</script>
