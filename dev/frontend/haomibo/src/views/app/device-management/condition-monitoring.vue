<style lang="scss">
  .device-monitoring {
    $item-height: calc((100vh - 350px) / 2);
    $item-width: 25% ;
    $item-padding: 20px;
    $item-extra-add-height: 50px;
    .main-without-tab {
      overflow-x: hidden!important;
    }

    .item-wrapper {
      position: relative;
      padding-bottom: $item-padding;
      padding-left: $item-padding;
      display: inline-block;
      width: $item-width;
      height: $item-height;
      & > .item {
        z-index: 1;
        border-radius: 0.3rem;
        border: solid 1px #eeeeee;
        background-color: white;
        position: relative;
        height: 100%;
        width: 100%;
        display: inline-block;
        cursor: pointer;
        &:hover {
          box-shadow: 1px 2px 0 #c6c6c6;
        }
        &.active {
          .item-header {
            border-bottom-color: #009900;
          }
        }
        .item-header {
          background: #f3f3f3;
          border-bottom: solid 2px #c6c6c6;
          height: 50px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0 20px 0 20px;
          .label {
            white-space: pre;
            font-size: 15px;
            color: #666666;
            max-width: 100%;
            flex-grow: 1;
            text-overflow: ellipsis;
            overflow: hidden;
          }
          .action-list {
            white-space: pre;
            img {
              width: 20px;
              margin-left: 0.5vw;
              img:first-child {
                margin-left: 0;
              }
            }

          }
        }
        .item-body {
          padding: 10px;
          .left-side {
            .action {
              button.btn {
                margin-bottom: 10px;
                white-space: pre;
                font-size: 11px;
                &.btn-success {
                  background-color: #49cf6f;
                  border-color: #49cf6f;
                  &:hover {
                    background-color: darken(#49cf6f, 8%);
                    border-color: darken(#49cf6f, 8%);
                  }
                }
                &.btn-info {
                  background-color: #1782d4;
                  &:hover {
                    background-color: darken(#1782d4, 8%);
                    border-color: darken(#1782d4, 8%);
                  }
                }
              }
            }
            .img {
              img {
                width: 5vw;
                object-fit: contain;
              }
            }
          }
          .right-side {
            .text-top {
              color: #1782d4;
              font-weight: bold;
              margin-bottom: 15px;
            }
            .content {
              & > div {
                display: flex;
                label {
                  white-space: pre;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  max-width: 100%;
                  color: #606266;
                  font-size: 12px;
                  line-height: 12px;
                  &:first-child {
                    width: 37%;
                    min-width: 37%;
                  }
                  &:last-child {
                    flex-grow: 1;
                  }
                  &.disabled {
                    color: #c0c0c0;
                  }
                }
              }

            }

            .caption {
              width: 37%;
            }
          }
        }
      }
      & > .item-extra-info {
        padding: 20px;
        opacity: 0;
        transition: 300ms;
        border-radius: 0.3rem;
        position: absolute;
        top: 0;
        width: 100%;
        height: 100%;
        left: 30px;
        background: black;
        z-index: 0;
        &>div {
          &>div {
            margin-bottom: 5px;
            align-items: center;
            &:first-child {
              width: 70px;
              color: white;
              white-space: pre;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            &:last-child {
              display: flex;
              align-items: center;
              flex-grow: 1;
              color: white;
              white-space: pre;
              overflow: hidden;
              text-overflow: ellipsis;
              img {
                width: 12px;
              }
              span {
                &.success {
                  color: #42b662;
                }
                &.pending {
                  color: #bbbbbb;
                }
                &.danger {
                  color: #e12c48;
                }
                margin-left: 5px;
                &.without {
                  margin-left: 18px;
                }
              }
              .chart-container {
                 width: 100%;
                 height: 100%;
               }
            }
          }
        }
      }
      &.slide-left {
        &>.item-extra-info {
          left: 0;
        }
        &:hover {
          & > .item-extra-info {
            left: calc(20px - 100%);
          }

        }
      }
      &:hover {
        & > .item {
          z-index: 4;
        }
        &> .item-extra-info {
          opacity: 0.9;
          transition: 300ms;
          left: 100%;
          z-index: 2;
        }

      }
    }
    .footer-pager {
      label {
        color: #666666;
      }
      .pagination {
        span {
          border: solid 1px #eeeeee;
          width: 25px;
          height: 25px;
          line-height: 23px;
          text-align: center;
          font-size: 12px;
          cursor: pointer;
          &.active{
            border: solid 1px #1782d4;
            background: #1782d4;
            color: white;
          }
          &:hover {
            border: solid 1px lighten(#1782d4,20%);
            background: lighten(#1782d4,20%);
            color: white;
          }
        }

      }
    }
  }

</style>
<template>
  <div class="device-monitoring">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.site')">
                  <b-form-select v-model="package" :options="packageData" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="deviceClassify" :options="deviceClassifyData" plain/>
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
            <b-button size="sm" class="ml-2" variant="info default">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
          </b-col>
        </b-row>

        <div class="flex-grow-1 m-0">
          <div class="item-wrapper" v-for="(item, index) in items"
               :class="index%4===0?(index===0?'pl-0':'p-0'):index%4===3?(index>4?'slide-left pb-0':'slide-left'):(index>4?'pb-0':'')">
            <div class="item d-flex flex-column">
              <div class="item-header">
                <div class="label">MW1000AA-001 00000dd</div>
                <div class="action-list">
                  <img src="../../../assets/img/icon_user_graphic.png">
                  <img src="../../../assets/img/icon_layout.png">
                  <img src="../../../assets/img/icon_bell.png">
                  <img src="../../../assets/img/icon_link.png">
                </div>
              </div>
              <div class="item-body flex-grow-1">
                <b-row class="h-100">
                  <b-col cols="4" class="left-side d-flex flex-column align-items-center justify-content-between">
                    <div class="action d-flex flex-column">
                      <b-button variant="info skyblue default" size="xs">空气难为</b-button>
                      <b-button variant="success default" size="xs">正常</b-button>
                    </div>
                    <div class="img">
                      <img src="../../../assets/img/small_device.png">
                    </div>
                  </b-col>
                  <b-col cols="8" class="right-side d-flex flex-column">
                    <label class="text-top">远行时长 18天 14:00:21</label>
                    <div class="flex-grow-1 d-flex content flex-column justify-content-end">
                      <div class="w-100">
                        <label>{{$t('device-management.site')}}:</label>
                        <label>通道001</label>
                      </div>
                      <div class="w-100">
                        <label>IP:</label>
                        <label>111.111.111.12</label>
                      </div>
                      <div class="w-100">
                        <label class="disabled">{{$t('device-management.no')}}:</label>
                        <label class="disabled">zhangshan</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-monitoring.landing-time')}}:</label>
                        <label>通道001</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.classify')}}:</label>
                        <label>人体查验</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.manufacture')}}:</label>
                        <label>同方威视</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-model')}}:</label>
                        <label>MW0001-001-000000</label></div>
                      <div class="w-100">
                        <label >{{$t('device-management.device-monitoring.disk-space')}}:</label>
                        <label></label>
                      </div>
                    </div>
                  </b-col>
                </b-row>
              </div>

            </div>
            <div class="item-extra-info flex-column d-flex">
              <div class="w-100 d-flex">
                <div>PLC:</div>
                <div><img src="../../../assets/img/radio_danger.png" /> <span class="danger">急停按下</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.main-acquire-card')}}:</div>
                <div><img src="../../../assets/img/radio_succss.png" /> <span class="success">急停弹起</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.from-acquire-card')}}:</div>
                <div><img src="../../../assets/img/radio_pending.png" /> <span class="pending">未知</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.servo')}}：</div>
                <div><span class="without">2000(mm)</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.slider-position')}}:</div>
                <div><img src="../../../assets/img/radio_danger.png" /> <span class="danger">就绪</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.emergency-stop')}}:</div>
                <div><img src="../../../assets/img/radio_pending.png" /> <span class="pending">急停按下</span> </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.footstep-alarm')}}:</div>
                <div><img src="../../../assets/img/radio_succss.png" /> <span class="success">不在线</span> </div>
              </div>
              <div class="w-100 d-flex flex-grow-1">
                <div>{{$t('device-management.device-monitoring.data-monitor')}}:</div>
                <div>
                  <div class="chart-container">
                    <line-chart :data="lineChartData" :height="100"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="d-flex align-items-center justify-content-between footer-pager">
            <label >共600条信息</label>
            <div class="pagination">
              <span class="left"><i class="icofont-simple-left"></i> </span>
              <span class="active">1</span>
              <span >2</span>
              <span >3</span>
              <span class="right"><i class="icofont-simple-right"></i></span>
            </div>
        </div>
      </div>
    </b-card>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import _ from 'lodash';
  import {getDirection} from "../../../utils";
  import Vue from 'vue'
  import LineChart from '../../../components/Charts/Line'
  import {lineChartData} from '../../../data/charts'

  export default {
    components: {
      'line-chart': LineChart,
    },
    data() {
      return {
        lineChartData,
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
    methods: {}
  }
</script>
