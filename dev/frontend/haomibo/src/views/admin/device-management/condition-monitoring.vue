<style lang="scss">
  @function calculateRem($size) {
    $remSize: $size / 16px;
    @return #{$remSize}rem;
  }

  .device-monitoring {
    $item-height: calc(50% - 0.3rem);
    $item-width: 25% ;
    $item-padding: calculateRem(20px);
    $item-extra-add-height: calculateRem(50px);
    .main-without-tab {
      overflow-x: hidden !important;
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
          height: calculateRem(50px);
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0 calculateRem(20px) 0 calculateRem(20px);
          .label {
            white-space: pre;
            font-size: calculateRem(15px);
            color: #666666;
            max-width: 100%;
            flex-grow: 1;
            text-overflow: ellipsis;
            overflow: hidden;
          }
          .action-list {
            white-space: pre;
            img {
              width: calculateRem(20px);
              margin-left: 0.5rem;
              &.disabled {
                filter: grayscale(1);
              }
              img:first-child {
                margin-left: 0;
              }
            }

          }
        }
        .item-body {
          padding: calculateRem(10px);
          .left-side {
            .action {
              button.btn {
                margin-bottom: calculateRem(10px);
                white-space: pre;
                font-size: calculateRem(11px);
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
              flex-grow: 1;
              display: flex;
              align-items: center;
              img {
                width: 90%;
                object-fit: contain;
              }
            }
          }
          .right-side {
            .text-top {
              color: #1782d4;
              font-weight: bold;
              margin-bottom: calculateRem(15px);
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
                  font-size: calculateRem(12px);
                  line-height: calculateRem(12px);
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
        padding: calculateRem(18px);
        opacity: 0;
        transition: 300ms;
        border-radius: 0.3rem;
        position: absolute;
        top: 0;
        width: 100%;
        height: 100%;
        left: calculateRem(30px);
        background: black;
        z-index: 0;
        & > div {
          & > div {
            margin-bottom: calculateRem(4px);
            align-items: center;
            &:first-child {
              width: calculateRem(75px);
              margin-bottom: 0;
              font-size: 0.7rem;
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
                width: calculateRem(12px);
              }
              span {
                font-size: 0.7rem;
                &.success {
                  color: #42b662;
                }
                &.pending {
                  color: #bbbbbb;
                }
                &.danger {
                  color: #e12c48;
                }
                margin-left: calculateRem(5px);
                &.without {
                  margin-left: calculateRem(18px);
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
        & > .item-extra-info {
          left: 0;
        }
        &:hover {
          & > .item-extra-info {
            left: calc(1.25rem - 100%);
          }

        }
      }
      &:hover {
        & > .item {
          z-index: 4;
        }
        & > .item-extra-info {
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
          width: calculateRem(25px);
          height: calculateRem(25px);
          line-height: calculateRem(23px);
          text-align: center;
          font-size: calculateRem(12px);
          cursor: pointer;
          &.active {
            border: solid 1px #1782d4;
            background: #1782d4;
            color: white;
          }
          &:hover:not(.disabled) {
            border: solid 1px lighten(#1782d4, 20%);
            background: lighten(#1782d4, 20%);
            color: white;
          }
          &.disabled {
            cursor: not-allowed;
            border: solid 1px #eeeeee;
            background: #eeeeee;
            color: #939394;
          }
        }

      }
    }
  }

  body.rtl {
    .device-monitoring {
      $item-height: calc(50% - 0.3rem);
      $item-width: 25% ;
      $item-padding: calculateRem(20px);
      $item-extra-add-height: calculateRem(50px);

      .item-wrapper {
        padding-right: $item-padding;
        padding-left: initial;
        & > .item {
          .item-header {
            background: #f3f3f3;
            border-bottom: solid 2px #c6c6c6;
            height: calculateRem(50px);
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 calculateRem(20px) 0 calculateRem(20px);
            .action-list {
              img {
                margin-left: initial;
                margin-right: 0.5rem;
                img:first-child {
                  margin-right: 0;
                  margin-left: initial;
                }
              }

            }
          }
        }
        & > .item-extra-info {
          left: unset;
          right: calculateRem(30px);
          background: black;
          z-index: 0;
          & > div {
            & > div {
              margin-bottom: calculateRem(4px);
              align-items: center;

              &:last-child {
                span {
                  margin-left: 0;
                  margin-right: calculateRem(5px);
                  &.without {
                    margin-left: 0;
                    margin-right: calculateRem(18px);
                  }
                }

              }
            }
          }
        }
        &.slide-left {
          & > .item-extra-info {
            left: unset;
            right: 0;
          }
          &:hover {
            & > .item-extra-info {
              left: unset;
              right: calc(1.25rem - 100%);
            }

          }
        }
        &:hover {
          & > .item {
            z-index: 4;
          }
          & > .item-extra-info {
            right: 100%;
            opacity: 0.9;
            left: unset;
            z-index: 2;
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
                  <b-form-select v-model="filter.fieldId" :options="siteSelectOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="filter.categoryId" :options="deviceCategoryOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-name')">
                  <b-form-input v-model="filter.deviceName"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
          </b-col>
        </b-row>

        <div class="flex-grow-1 m-0">
          <div class="item-wrapper" v-for="(item, index) in items"
               :class="index%4===0?(index===0?'pl-0':'pl-0'):index%4===3?(index>4?'slide-left':'slide-left'):(index>4?'':'')">
            <div class="item d-flex flex-column">
              <div class="item-header">
                <div class="label">{{item.deviceNumber}}</div>
                <div class="action-list">
                  <img v-if="item.maxScanCount > item.deviceTrafficHigh" src="../../../assets/img/icon_user_graphic.png">
                  <img v-else src="../../../assets/img/icon_user_graphic_disabled.png">
                  <img v-if="item.deviceStorageAlarm === 0" src="../../../assets/img/icon_layout.png">
                  <img v-else src="../../../assets/img/icon_layout_disabled.png">
                  <img v-if="item.plcStatus == '0' && item.slaveCardStatus == '0' && item.masterCardStatus == '0' && item.footWarning == '0' && item.footWarning == '0'" src="../../../assets/img/icon_bell.png">
                  <img v-else src="../../../assets/img/icon_bell_disabled.png">
                  <img v-if="item.deviceOnline === 0" src="../../../assets/img/icon_link.png">
                  <img v-else src="../../../assets/img/icon_link_disabled.png">
                </div>
              </div>
              <div class="item-body flex-grow-1">
                <b-row class="h-100">
                  <b-col cols="4" class="left-side d-flex flex-column align-items-center justify-content-between">
                    <div class="action d-flex flex-column">
                      <b-button variant="info skyblue default" size="xs">{{item.currentWorkFlowName}}</b-button>
                      <b-button class="default" size="xs" :variant="['WaitToStart','Preparing'].includes(item.currentStatus)?'info skyblue':
                      ['Executing','OkFinished','OkReady'].includes(item.currentStatus)?'success':
                      ['WarnContinuing','WarnFinished'].includes(item.currentStatus)?'warning':'danger'">{{item.currentStatusName}}</b-button>
                    </div>
                    <div class="img">
                      <img v-if="item.imageUrl" :src="item.imageUrl">
                      <img v-else src="../../../assets/img/small_device.png">
                    </div>
                  </b-col>
                  <b-col cols="8" class="right-side d-flex flex-column">
                    <label class="text-top">{{$t('device-management.device-monitoring.running-time')}} {{item.runningTimeValue}}</label>
                    <div class="flex-grow-1 d-flex content flex-column justify-content-end">
                      <div class="w-100">
                        <label>{{$t('device-management.site')}}:</label>
                        <label>{{item.fieldName}}</label>
                      </div>
                      <div class="w-100">
                        <label>IP:</label>
                        <label>{{item.ipAddress}}</label>
                      </div>
                      <div class="w-100">
                        <label class="disabled">{{$t('device-management.no')}}:</label>
                        <label :class="item.account===null?`disabled`:``">{{item.account}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-monitoring.landing-time')}}:</label>
                        <label>{{item.landTime}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.classify')}}:</label>
                        <label>{{item.category}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.manufacture')}}:</label>
                        <label>{{item.manufacturerName}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-model')}}:</label>
                        <label>{{item.originalModel}}</label></div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-monitoring.disk-space')}}:</label>
                        <label>{{item.diskSpace}}(GB)</label>
                      </div>
                    </div>
                  </b-col>
                </b-row>
              </div>

            </div>
            <div class="item-extra-info flex-column d-flex">
              <div class="w-100 d-flex">
                <div>PLC:</div>
                <div v-if="item.plcStatus === '1'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.plcStatusName}}</span></div>
                <div v-else-if="item.plcStatus === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.plcStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.plcStatusName}}</span>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.main-acquire-card')}}:</div>
                <div v-if="item.masterCardStatus === '1'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.masterCardStatusName}}</span></div>
                <div v-else-if="item.masterCardStatus === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.masterCardStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.masterCardStatusName}}</span>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.from-acquire-card')}}:</div>
                <div v-if="item.slaveCardStatus === '1'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.slaveCardStatusName}}</span></div>
                <div v-else-if="item.slaveCardStatus === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.slaveCardStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.slaveCardStatusName}}</span>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.servo')}}：</div>
                <div v-if="item.servo === '1'"><img src="../../../assets/img/radio_succss.png"/> <span class="success">{{item.servoName}}</span>
                </div>
                <div v-else-if="item.servo === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.servoName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span
                  class="pending">{{item.servoName}}</span></div>

              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.slider-position')}}:</div>
                <div><span class="without">{{item.slidePosition}}(mm)</span></div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.emergency-stop')}}:</div>
                <div v-if="item.emergencyStop === '1'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.emergencyStopName}}</span></div>
                <div v-else-if="item.emergencyStop === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.emergencyStopName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.emergencyStopName}}</span>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.footstep-alarm')}}:</div>
                <div v-if="item.footWarning === '1'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.footWarningName}}</span></div>
                <div v-else-if="item.footWarning === '0'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.footWarningName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.footWarningName}}</span>
                </div>
              </div>
              <div class="w-100 d-flex flex-grow-1">
                <div>{{$t('device-management.device-monitoring.data-monitor')}}:</div>
                <div>
                  <div class="chart-container">
                    <v-chart :options="item.lineChartOptions" style="height: 5rem; width: 100%" :autoresize="true"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="d-flex align-items-center justify-content-between footer-pager">
          <label>{{$t('vuetable.total')}} {{pagination.total}} {{$t('vuetable.record')}}</label>
          <div class="pagination">
            <span @click="onFirstPage()" :class="pagination.currentPage === 1?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-double-left':'icofont-double-right'"></i> </span>
            <span class="left" @click="onPrevPage()" :class="pagination.currentPage === 1?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-simple-left':'icofont-simple-right'"></i> </span>
            <template v-if="pagination.lastPage <= 5">
              <span @click="goToPage(pageNum)" v-for="pageNum in pagination.lastPage"
                    :class="pageNum === pagination.currentPage?`active`:``">{{pageNum}}</span>
            </template>
            <template v-if="pagination.lastPage > 5">
              <span @click="goToPage(pageNum)" v-for="pageNum in pageRange()"
                    :class="pageNum === pagination.currentPage?`active`:``">{{pageNum}}</span>
            </template>
            <span class="right" @click="onNextPage()"
                  :class="pagination.currentPage === pagination.lastPage?`disabled`:``"><i
              :class="direction === 'rtl'?'icofont-simple-left':'icofont-simple-right'"></i></span>
            <span @click="onLastPage()" :class="pagination.currentPage === pagination.lastPage?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-double-right':'icofont-double-left'"></i> </span>
          </div>
        </div>
      </div>
    </b-card>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import {getDirection} from "../../../utils";
  import Vue from 'vue'
  import ECharts from 'vue-echarts'
  import 'echarts/lib/chart/line';
  import {getApiManager, getDateTimeWithFormat} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';

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

  let findDicTextData = (options, value, flag = true) => {
    let name = null;
    if (options == null || options.length === 0)
      return name;
    options.forEach(option => {
      if (option.value === value)
        name = flag ? option.text : option;
    });
    return name;
  };

  export default {
    components: {
      'v-chart': ECharts
    },
    mounted() {
      this.getCategoryData();
      this.getSiteData();
      this.getDataFetch();
    },
    data() {
      return {
        manufacturerDicData: {
          '0': "同方威视",
          '1': "海康威视",
          '2': "大华股份",
          '3': "华为"
        },
        currentFlowDicData: [
          {text: "扫描", value: "Rescan"},
          {text: "空气校准", value: 'AirCalibrate'},
        ],
        currentStatusDicData: [
          {text: "未执行", value: "Preparing"},
          {text: "执行中", value: "WarnContinuing"},
          {text: "正常待机", value: "OkFinished"},
          {text: "停止执行", value: "ErrorStopped"},
        ],
        deviceStatusDicData: [
          {text: "急停按下", value: "0"},
          {text: "急停弹起", value: "1"},
          {text: "未知", value: "-1"},
        ],
        servoStatusDicData: [
          {text: "就绪", value: "0"},
          {text: "未就绪", value: "1"},
          {text: "未知", value: "-1"},
        ],
        footStatusDicData: [
          {text: "在线", value: "0"},
          {text: "不在线", value: "1"},
          {text: "未知", value: "-1"},
        ],
        siteData: [],
        categoryData: [],
        deviceCategoryOptions: [],
        siteSelectOptions: [],
        filter: {
          fieldId: null,
          categoryId: null,
          deviceName: null
        },
        pagination: {
          perPage: 8,
          total: 0,
          currentPage: 1,
          lastPage: 0,
          from: 1,
          to: 1
        },
        direction: getDirection().direction,
        items: [],
        chartOption: {
          tooltip: {
            trigger: 'axis'
          },
          color: '#0b78ff',
          toolbox: {
            show: false
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            axisLabel:{
              fontSize:8,
              color:'white'
            },
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}',
              fontSize:8,
              color:'white'
            },
            splitLine: {
              lineStyle :{
                type: 'dotted',
                color:'#403738'
              }
            },
          },
          series: [
            {
              name: this.$t('device-management.device-monitoring.data-monitor'),
              type: 'line',
              data: [11, 11, 15, 13, 12, 13, 10]
            },
            {
              name: this.$t('system-setting.parameter-setting.security-instrument-flow-high'),
              type: 'line',
              lineStyle : {
                color: 'red',
                type : 'dotted'
              },
              data: [],
            },
            {
              name: this.$t('system-setting.parameter-setting.security-instrument-flow-middle'),
              type: 'line',
              lineStyle : {
                color: 'yellow',
                type : 'dotted'
              },
              data: [],
            }
          ]
        }
      }
    },
    methods: {
      generateChartData(data,hValue,mValue) {
        let xValues = data.timeList;
        let yValues = data.countList;
        let hValues = [];
        let mValues = [];
        yValues.forEach(index => {
          hValues.push(hValue);
          mValues.push(mValue);
        });
        let option = this.chartOption;
        option.xAxis.data = xValues;
        option.series[0].data = yValues;
        option.series[1].data = hValues;
        option.series[2].data = mValues;
        return option;
      },
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
      onSearchButton() {
        this.getDataFetch();
      },
      onResetButton() {
        this.filter = {
          fieldId: null,
          categoryId: null,
          deviceName: null
        };
      },
      transformData(data) {
        let result = [];
        this.pagination.total = data.total;
        this.pagination.lastPage = data.last_page;
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          temp.deviceNumber = temp.device ? temp.device.deviceSerial : 'Unknown';
          temp.fieldName = temp.device && temp.device.field ? temp.device.field.fieldDesignation : '';
          temp.landTime = getDateTimeWithFormat(temp.loginTime, 'monitor');
          temp.category = temp.device ? temp.device.archive.archiveTemplate.deviceCategory.categoryName : '';
          temp.manufacturerName = temp.manufacturer ? this.manufacturerDicData[temp.manufacturer] : '';
          temp.currentWorkFlowName = findDicTextData(this.currentFlowDicData, temp.currentWorkFlow);
          temp.currentStatusName = findDicTextData(this.currentStatusDicData, temp.currentStatus);
          temp.imageUrl = temp.device && temp.device.imageUrl ? apiBaseUrl + temp.device.imageUrl : null;
          temp.plcStatusName = findDicTextData(this.deviceStatusDicData, temp.plcStatus);
          temp.masterCardStatusName = findDicTextData(this.deviceStatusDicData, temp.masterCardStatus);
          temp.slaveCardStatusName = findDicTextData(this.deviceStatusDicData, temp.slaveCardStatus);
          temp.servoName = findDicTextData(this.servoStatusDicData, temp.servo);
          temp.servoName = findDicTextData(this.servoStatusDicData, temp.servo);
          temp.emergencyStopName = findDicTextData(this.deviceStatusDicData, temp.emergencyStop);
          temp.footWarningName = findDicTextData(this.footStatusDicData, temp.footWarning);
          temp.lineChartOptions = this.generateChartData(temp.record,temp.deviceTrafficHigh,temp.deviceTrafficMiddle);
          temp.maxScanCount = Math.max.apply(Math,temp.record.countList);
          temp.runningTimeValue = getDateTimeWithFormat(temp.deviceLoginTime,'monitor-diff',this.$i18n.locale);
          result.push(temp);
        }
        this.items = result;
      },
      getDataFetch() { // customize data loading for table from server
        getApiManager().post(`${apiBaseUrl}/device-management/condition-monitoring/get-by-filter-and-page`,
          {
            currentPage: this.pagination.currentPage,
            perPage: this.pagination.perPage,
            filter: this.filter
          }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.transformData(data);
              break;
          }
        });
      },
      //pagination methods
      pageRange() {
        let min = 0, max = 0;
        min = this.pagination.currentPage < 3 ? 1 :
          this.pagination.lastPage - this.pagination.currentPage < 2 ?
            5 - this.pagination.lastPage + this.pagination.currentPage : this.pagination.currentPage - 2;
        let array = [], j = 0;
        max = this.pagination.lastPage > min + 4 ? min + 4 : this.pagination.lastPage;
        min = max - 4;
        for (let i = min; i <= max; i++) {
          array[j] = i;
          j++;
        }
        return array;
      },
      goToPage(pageNum) {
        this.pagination.currentPage = pageNum;
      },
      onFirstPage() {
        this.pagination.currentPage = 1;
      },
      onLastPage() {
        this.pagination.currentPage = this.pagination.lastPage;
      },
      onPrevPage() {
        if (this.pagination.currentPage === 1)
          return;
        this.pagination.currentPage--;
      },
      onNextPage() {
        if (this.pagination.currentPage === this.pagination.lastPage)
          return;
        this.pagination.currentPage++;
      }
    },
    watch: {
      'pagination.currentPage': function (newVal, oldVal) {
        this.getDataFetch();
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
        this.deviceCategoryOptions.push({value: null, text: `${this.$t('permission-management.all')}`});
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
          value: org.fieldId,
          html: `${generateSpace(getLevel(org))}${org.fieldDesignation}`
        }));
        this.siteSelectOptions.push({
          value: null,
          html: `${this.$t('permission-management.all')}`
        });
      },
    }
  }
</script>
