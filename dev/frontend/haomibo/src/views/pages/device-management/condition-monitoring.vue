<style lang="scss">
  @function calculateRem($size) {
    $remSize: $size / 16px;
    @return #{$remSize}rem;
  }

  .no-item {
    background-color: #e9e9e9;
    padding-top: 30vh;
  }
  .no-item-data {
    font-size: 1rem;
    color: silver;
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
      height: fit-content !important;
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
              width: 65px;
              height: 94px;
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
                  overflow: visible;
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
  .item-wrapper-height{
    position: relative;
    padding-bottom: 1.25rem;
    padding-left: 1.25rem;
    display: inline-block;
    width: 25%;
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
    <b-card v-show="!isLoading" class="main-without-tab">
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
                  <b-form-select v-model="filter.categoryId" :options="deviceCategoryOption" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-name')">
                  <b-form-input v-model="filter.deviceName"/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
          </b-col>
        </b-row>

        <div class="flex-grow-1 m-0" v-if="isExist">

          <div class="item-wrapper" v-for="(item, index) in items"
               :class="index%4===0?(index===0?'pl-0':'pl-0'):index%4===3?(index>4?'slide-left':'slide-left'):(index>4?'':'')">
            <div class="item d-flex flex-column" @click="onDetailClick(item.statusId, index)">
              <div :style="item.device.currentStatus !=='1000002002'?'border-bottom: solid 2px #74dc21;':'border-bottom: solid 2px #c6c6c6;'" class="item-header">
                <div class="label">{{item.deviceNumber}}</div>
                <div class="action-list">
                  <img v-if="item.device.deviceType === '1000001901' && item.maxScanCount > item.deviceTrafficHigh && item.device.currentStatus !=='1000002002'"
                       src="../../../assets/img/icon_user_graphic.png">
                  <img v-else-if="item.device.deviceType === '1000001901'" src="../../../assets/img/icon_user_graphic_disabled.png">
                  <img v-if="item.device.deviceType === '1000001901' && item.deviceStorageAlarm === 0 && item.device.currentStatus !=='1000002002'" src="../../../assets/img/icon_layout.png">
                  <img v-else-if="item.device.deviceType === '1000001901'" src="../../../assets/img/icon_layout_disabled.png">
                  <img
                    v-if="(item.device.deviceType === '1000001901' && item.plcStatus === '0' && item.slaveCardStatus === '0' && item.masterCardStatus === '0' && item.footWarning === '0' && item.footWarning === '0') || (item.device.deviceType === '1000001901' && item.device.currentStatus ==='1000002002')"
                    src="../../../assets/img/icon_bell_disabled.png">
                  <img v-else-if="item.device.deviceType === '1000001901'" src="../../../assets/img/icon_bell.png">
                  <img v-if="item.device.currentStatus !=='1000002002'" src="../../../assets/img/icon_link.png">
                  <img v-else src="../../../assets/img/icon_link_disabled.png">
                </div>
              </div>
              <div class="item-body flex-grow-1">
                <b-row class="h-100">
                  <b-col cols="4" class="left-side d-flex flex-column align-items-center justify-content-between">
                    <div class="action d-flex flex-column">
                      <b-button v-if="item.device.deviceType === '1000001901' && item.device.currentStatus !=='1000002002'" variant="info skyblue default" size="xs">{{item.currentWorkFlowName}}</b-button>
                      <b-button v-else variant="info skyblue default" size="xs" style="opacity: 0"> {{item.currentWorkFlowName}}</b-button>
                      <b-button v-if="item.device.deviceType === '1000001901' && item.device.currentStatus !=='1000002002'" class="default" size="xs" :variant="['0','1'].includes(item.currentStatus)?'info skyblue':
                      ['2','3','4'].includes(item.currentStatus)?'success':
                      ['5','6'].includes(item.currentStatus)?'warning':'danger'">{{item.currentStatusName}}</b-button>
                      <b-button v-else class="default" size="xs" style="opacity: 0" :variant="['0','1'].includes(item.currentStatus)?'info skyblue':
                      ['2','3','4'].includes(item.currentStatus)?'success':
                      ['5','6'].includes(item.currentStatus)?'warning':'danger'">{{item.currentStatusName}}</b-button>
                    </div>
                    <div class="img">
                      <img v-if="item.imageUrl" :src="item.imageUrl">
                      <img v-else-if="item.device.deviceType === '1000001901'" src="../../../assets/img/small_device.png">
                      <img v-else src="../../../assets/img/small_tablet.png">
                    </div>
                  </b-col>
                  <b-col cols="8" class="right-side d-flex flex-column" style="padding-left: 0; padding-right: 2px;">
                    <label v-if="(item.device.status === '1000000701' && item.device.currentStatus !=='1000002002')" class="text-top">{{$t('device-management.device-monitoring.running-time')}}
                      {{item.runningTimeValue}}</label>
                    <label v-else class="text-top" style="opacity: 0">{{$t('device-management.device-monitoring.running-time')}}</label>
                    <div class="flex-grow-1 d-flex content flex-column justify-content-end">
                      <div class="w-100">
                        <label>{{$t('device-management.site')}}:</label>
                        <label>{{item.fieldName}}</label>
                      </div>
                      <div class="w-100">
                        <label>IP:</label>
                        <label v-if="item.device.currentStatus !=='1000002002'">{{item.ipAddress}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.number')}}:</label>
                        <label v-if="(item.device.currentStatus ==='1000002003' || item.device.currentStatus ==='1000002005')">{{item.account}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.device-monitoring.landing-time')}}:</label>
                        <label v-if="(item.device.currentStatus ==='1000002003' || item.device.currentStatus ==='1000002005')">{{item.landTime}}</label>
                      </div>
                      <div class="w-100">
                        <label>{{$t('device-management.classify')}}:</label>
                        <label>{{item.category}}</label>
                      </div>
                      <div class="w-100">
                        <label style="width: fit-content">{{$t('device-management.manufacture')}}:</label>
                        <label>{{item.manufacturerName}}</label>
                      </div>
                      <div class="w-100">
                        <label style="width: fit-content">{{$t('device-management.device-model')}}:</label>
                        <label>{{item.device.archive.archiveTemplate.originalModel}}</label></div>
                      <div class="w-100" :style="{opacity:item.device.deviceType === '1000001901'?1:0}">
                        <label>{{$t('device-management.device-monitoring.disk-space')}}:</label>
                        <label v-if="item.device.currentStatus !=='1000002002' && item.device.status === '1000000701'">{{item.diskSpace}}(GB)</label>
                      </div>
                    </div>
                  </b-col>
                </b-row>
              </div>

            </div>
            <div  v-if="item.device.deviceType === '1000001901' && item.statusId===selectedId" class="item-extra-info flex-column d-flex">
              <div class="w-100 d-flex">
                <div>PLC:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div v-if="item.plcStatus === '0'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.plcStatusName}}</span></div>
                <div v-else-if="item.plcStatus === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.plcStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.plcStatusName}}</span>
                </div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.main-acquire-card')}}:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div v-if="item.masterCardStatus === '0'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.masterCardStatusName}}</span></div>
                <div v-else-if="item.masterCardStatus === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.masterCardStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.masterCardStatusName}}</span>
                </div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.from-acquire-card')}}:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div v-if="item.slaveCardStatus === '0'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.slaveCardStatusName}}</span></div>
                <div v-else-if="item.slaveCardStatus === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.slaveCardStatusName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.slaveCardStatusName}}</span>
                </div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.servo')}}：</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div v-if="item.servo === '0'"><img src="../../../assets/img/radio_succss.png"/> <span class="success">{{item.servoName}}</span>
                </div>
                <div v-else-if="item.servo === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.servoName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span
                  class="pending">{{item.servoName}}</span></div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.slider-position')}}:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div><span class="without">{{item.slidePosition}}(mm)</span></div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.emergency-stop')}}:</div>
                <div v-if="item.device.currentStatus!=='1000002002'">
                <div v-if="item.emergencyStop === '0'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.emergencyStopName}}</span></div>
                <div v-else-if="item.emergencyStop === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.emergencyStopName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.emergencyStopName}}</span>
                </div>
                </div>
              </div>
              <div class="w-100 d-flex">
                <div>{{$t('device-management.device-monitoring.footstep-alarm')}}:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                <div v-if="item.footWarning === '0'"><img src="../../../assets/img/radio_succss.png"/> <span
                  class="success">{{item.footWarningName}}</span></div>
                <div v-else-if="item.footWarning === '1'"><img src="../../../assets/img/radio_danger.png"/> <span
                  class="danger">{{item.footWarningName}}</span></div>
                <div v-else><img src="../../../assets/img/radio_pending.png"/> <span class="pending">{{item.footWarningName}}</span>
                </div>
                </div>
              </div>
              <div v-if="item.device.deviceType === '1000001901'" class="w-100 d-flex flex-grow-1">
                <div>{{$t('device-management.device-monitoring.data-monitor')}}:</div>
                <div v-if="item.device.currentStatus !=='1000002002'">
                  <div class="chart-container">
                    <v-chart :options="lineChartOption" style="height: 80px; width: 100%" :autoresize="true"/>
                  </div>
                </div>
                <div v-else>
                  <div class="chart-container">
                    <v-chart :options="lineChartOption" style="height: 80px; width: 100%; opacity: 0" :autoresize="true"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="flex-grow-1 m-0 no-item" v-if="!isExist">
          <div class="text-center no-item-data">{{$t('vuetable.no-data')}}</div>
        </div>
        <div class="d-flex align-items-center justify-content-between footer-pager">
<!--          <label>{{$t('vuetable.total')}} {{pagination.total}} {{$t('vuetable.record')}}</label>-->
          <div class="pagination">
            <span @click="onFirstPage()" :class="pagination.currentPage === 1?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-double-left':'icofont-double-right'"/> </span>
            <span class="left" @click="onPrevPage()" :class="pagination.currentPage === 1?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-simple-left':'icofont-simple-right'"/> </span>
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
              :class="direction === 'rtl'?'icofont-simple-left':'icofont-simple-right'"/></span>
            <span @click="onLastPage()" :class="pagination.currentPage === pagination.lastPage?`disabled`:``"><i
              :class="direction === 'ltr'?'icofont-double-right':'icofont-double-left'"/> </span>

          </div>
                      <div style="display: flex; justify-content: flex-end;align-items: center;">

                        <div>
                          <span class="mr-3">{{$t('vuetable.total')}}</span>
                          <span class="mr-3 font-weight-bold">{{pagination.total}}</span>
                          <span class="mr-3">{{$t('vuetable.record')}}</span>
                        </div>
                      </div>
        </div>
      </div>
    </b-card>
    <div v-show="isLoading" class="loading"></div>
  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import {
    checkPermissionItem,
    getDeviceDicDataByDicIdForOptions,
    getDicDataByDicIdForOptions,
    getDirection
  } from "../../../utils";
  import Vue from 'vue'
  import ECharts from 'vue-echarts'
  import 'echarts/lib/chart/line';
  import {getApiManager, getApiManagerError, getDateTimeWithFormat} from '../../../api';
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
    created() {
      //this.onSearchButton();


      //this.timer = setInterval(() => this.onTaskVuetableChangePage(this.httpOption.params.page), 15000);
      //this.timer = setInterval(() => this.transform(this.taskVuetableHttpFetch(this.apiUrl, this.httpOption)), 15000);

    },
    beforeDestroy() {
      clearInterval(this.timer);
      clearInterval(this.timerDetail);
    },
    mounted() {
      this.getCategoryData();
      this.getSiteData();
      this.getDataFetch(false);
      this.getDeviceDicData();
    },
    data() {
      return {
        isLoading: false,
        selectedId : null,
        selectedIndex : null,
        isExist:true,
        detailDevice:false,
        manufacturerDicData: [],
        currentFlowDicData: [],
        currentStatusDicData: [],
        deviceStatusDicData: [
          {text: "连接", value: "0"},
          {text: "未连接", value: "1"},
          {text: "未知", value: "-1"},
        ],
        stopStatusDicData: [
          {text: "急停按下", value: "1"},
          {text: "急停弹起", value: "0"},
          {text: "未知", value: "-1"},
        ],
        servoStatusDicData: [
          {text: "就绪", value: "0"},
          {text: "不就绪", value: "1"},
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
        deviceCategoryOption: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '2', text: this.$t('log-management.device-log.judge')},
          {value: '3', text: this.$t('log-management.device-log.manual')},
          {value: '4', text: this.$t('log-management.device-log.hand')}
        ],
        siteSelectOptions: [],
        filter: {
          fieldId: null,
          categoryId: null,
          deviceName: null
        },
        saveFilter: {
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
        guidList: [],
        lineChartOption :[],
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
            axisLabel: {
              fontSize: 8,
              color: 'white'
            },
            data: []
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}',
              fontSize: 8,
              color: 'white'
            },
            splitLine: {
              lineStyle: {
                type: 'dotted',
                color: '#403738'
              }
            },
          },
          series: [
            {
              name: this.$t('device-management.device-monitoring.data-monitor'),
              type: 'line',
              data: []
            },
            {
              name: this.$t('system-setting.parameter-setting.security-instrument-flow-high'),
              type: 'line',
              lineStyle: {
                color: 'red',
                type: 'dotted'
              },
              data: []
            },
            {
              name: this.$t('system-setting.parameter-setting.security-instrument-flow-middle'),
              type: 'line',
              lineStyle: {
                color: 'yellow',
                type: 'dotted'
              },
              data: []
            }
          ]
        }
      }
    },
    methods: {
      autoUpdate() {
        this.getDataFetchRefresh();
        if(this.selectedId!== null){
          //this.onDetailClick(this.selectedId, this.selectdeIndex);
        }
      },
        getDetailInformation(statusId, index) {
            getApiManagerError().post(`${apiBaseUrl}/device-management/condition-monitoring/get-by-id`, {
                statusId: statusId
            }).then((response) => {
                let message = response.data.message;
                let data = response.data.data;
                switch (message) {
                    case responseMessages['ok']:
                        this.lineChartOption = this.generateChartData(data.record, data.deviceTrafficHigh, data.deviceTrafficMiddle);
                        //this.items[index].lineChartOptions = this.generateChartData(data.record, data.deviceTrafficHigh, data.deviceTrafficMiddle);
                        //this.items[index].lineChartOptions = this.chartOption;
                        this.items[index].maxScanCount = Math.max.apply(Math, data.record.countList);
                        //this.items[index].deviceNumber = data.device.deviceName;

                        //this.items[index].fieldName = data.device && data.device.field ? data.device.field.fieldDesignation : '';
                        //this.items[index].landTime = getDateTimeWithFormat(data.loginTime, 'monitor');
                        //this.items[index].category = data.device.category ? data.device.category.categoryName : '';
                        //this.items[index].manufacturerName = data.device.archive && data.device.archive.archiveTemplate ? findDicTextData(this.manufacturerDicData, data.device.archive.archiveTemplate.manufacturer):'';
                        //this.items[index].currentWorkFlowName = findDicTextData(this.currentFlowDicData, data.currentWorkFlow);
                        //this.items[index].currentStatusName = findDicTextData(this.currentStatusDicData, data.currentStatus);
                        //this.items[index].imageUrl = data.device && data.device.imageUrl ? data.device.imageUrl : null;



                        // this.items[index].device = data.device;
                        // this.items[index].plcStatusName = findDicTextData(this.deviceStatusDicData, data.plcStatus);
                        // this.items[index].masterCardStatusName = findDicTextData(this.deviceStatusDicData, data.masterCardStatus);
                        // this.items[index].slaveCardStatusName = findDicTextData(this.deviceStatusDicData, data.slaveCardStatus);
                        // this.items[index].servoName = findDicTextData(this.servoStatusDicData, data.servo);
                        // //this.items[index].servoName = findDicTextData(this.servoStatusDicData, data.servo);
                        // this.items[index].emergencyStopName = findDicTextData(this.stopStatusDicData, data.emergencyStop);
                        // this.items[index].footWarningName = findDicTextData(this.footStatusDicData, data.footWarning);
                        //
                        // //this.items[index].runningTimeValue = getDateTimeWithFormat(data.loginTime, 'monitor-diff', this.$i18n.locale);
                        // //this.items[index].deviceTrafficHigh = data.deviceTrafficHigh;
                        // //this.items[index].deviceStorageAlarm = data.deviceStorageAlarm;
                        // this.items[index].plcStatus = data.plcStatus;
                        // this.items[index].slaveCardStatus = data.slaveCardStatus;
                        // this.items[index].masterCardStatus = data.masterCardStatus;
                        // this.items[index].footWarning = data.footWarning;
                        // //this.items[index].deviceOnline = data.deviceOnline;
                        // this.items[index].servo = data.servo;
                        //this.items[index].slidePosition = data.slidePosition;
                        // this.items[index].emergencyStop = data.emergencyStop;

                        break;
                }
            });
        },
      onDetailClick(statusId, index){
        clearInterval(this.timerDetail);
        this.selectedId = statusId;
        this.selectdeIndex = index;
        clearInterval(this.timerDetail);
        this.getDetailInformation(statusId, index);
        this.timerDetail = setInterval(() => this.getDetailInformation(this.selectedId, this.selectdeIndex), 1000 * 60 * 5);
        //this.items[index].detailStatus = true;
      },
      getDeviceDicData() {
        this.manufacturerDicData = getDicDataByDicIdForOptions(9);
        this.currentFlowDicData = getDeviceDicDataByDicIdForOptions(10);
        this.currentStatusDicData = getDeviceDicDataByDicIdForOptions(11);
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      generateChartData(data, hValue, mValue) {

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
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-classify/category/get-all`, {
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
        getApiManagerError().post(`${apiBaseUrl}/site-management/field/get-all`, {
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
        this.saveFilter.fieldId = this.filter.fieldId;
        this.saveFilter.categoryId = this.filter.categoryId;
        this.saveFilter.deviceName = this.filter.deviceName;
        this.pagination.currentPage = 1;
        this.getDataFetch();
      },
      onResetButton() {
        this.filter = {
          fieldId: null,
          categoryId: null,
          deviceName: null
        };
        this.saveFilter = {
            fieldId: null,
            categoryId: null,
            deviceName: null
        };
      },
      transformData(data) {
        let result = [];
        this.pagination.total = data.total;
        this.pagination.lastPage = data.last_page;
        if(data.total===0){
          this.isExist = false;
        }
        else {
          this.isExist = true;
          let temp;
          this.guidList = [];
          for (let i = 0; i < data.data.length; i++) {
            temp = data.data[i];
            if (temp.device == null)
              continue;
            this.guidList.push(temp.device.guid);
            temp.deviceNumber = temp.device ? temp.device.deviceName : 'Unknown';
            temp.fieldName = temp.device && temp.device.field ? temp.device.field.fieldDesignation : '';
            temp.landTime = getDateTimeWithFormat(temp.loginTime, 'monitor');
            temp.category = temp.device ? temp.device.category.categoryName : '';
            temp.manufacturerName = temp.device && temp.device.archive ? findDicTextData(this.manufacturerDicData, temp.device.archive.archiveTemplate.manufacturer) : '';
            temp.currentWorkFlowName = findDicTextData(this.currentFlowDicData, temp.currentWorkFlow);
            temp.currentStatusName = findDicTextData(this.currentStatusDicData, temp.currentStatus);
            temp.imageUrl = temp.device && temp.device.imageUrl ? temp.device.imageUrl : null;
            temp.plcStatusName = findDicTextData(this.deviceStatusDicData, temp.plcStatus);
            temp.masterCardStatusName = findDicTextData(this.deviceStatusDicData, temp.masterCardStatus);
            temp.slaveCardStatusName = findDicTextData(this.deviceStatusDicData, temp.slaveCardStatus);
            temp.servoName = findDicTextData(this.servoStatusDicData, temp.servo);
            //temp.servoName = findDicTextData(this.servoStatusDicData, temp.servo);
            temp.emergencyStopName = findDicTextData(this.stopStatusDicData, temp.emergencyStop);
            temp.footWarningName = findDicTextData(this.footStatusDicData, temp.footWarning);
            //temp.lineChartOptions = this.generateChartData(temp.record, temp.deviceTrafficHigh, temp.deviceTrafficMiddle);
            //temp.maxScanCount = Math.max.apply(Math, temp.record.countList);
            temp.lineChartOptions = [];
            temp.maxScanCount = [];
            temp.runningTimeValue = getDateTimeWithFormat(temp.deviceLoginTime, 'monitor-diff', this.$i18n.locale);
            temp.detailStatus = false;
            result.push(temp);
          }
          this.items = result;
          this.timer = setInterval(() => this.autoUpdate(), 1000);
        }
      },
      getDataFetch() { // customize data loading for table from server
        this.selectedId = null;
        clearInterval(this.timer);
        clearInterval(this.timerDetail);

        //this.isLoading = true;
        //let current = false;
        getApiManager().post(`${apiBaseUrl}/device-management/condition-monitoring/get-by-filter-and-page`,
          {
            currentPage: this.pagination.currentPage,
            perPage: this.pagination.perPage,
            filter: this.saveFilter
          }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.transformData(data);
              //this.isLoading = false;
              break;
          }
          //this.isLoading = false;
        });
        //this.isLoading = false;
      },
      findIndex(guid) {
        for (let i = 0; i < this.items.length; i++) {
            if(this.items[i].device.guid == guid) {
                return i;
            }
        }
        return -1;
      },
      changeDetailData(index, data) {
        if(data.device != null) {
            this.items[index].device.currentStatus = data.device.currentStatus;
        }

        if(data.ipAddress != null) {
            this.items[index].ipAddress = data.ipAddress;
        }
        if(data.account != null) {
            this.items[index].account = data.account;
        }
        if(data.loginTime != null) {
            this.items[index].landTime = getDateTimeWithFormat(data.loginTime, 'monitor');
        }
        if(data.deviceLoginTime != null) {
            this.items[index].runningTimeValue = getDateTimeWithFormat(data.deviceLoginTime, 'monitor-diff', this.$i18n.locale);
        }
        if(data.diskSpace != null) {
            this.items[index].diskSpace = data.diskSpace;
        }
        if(data.currentWorkFlow != null) {
            this.items[index].currentWorkFlowName = findDicTextData(this.currentFlowDicData, data.currentWorkFlow);
        }
        if(data.currentStatus != null) {
            this.items[index].currentStatusName = findDicTextData(this.currentStatusDicData, data.currentStatus);
        }

        if(data.plcStatus != null) {
            this.items[index].plcStatusName = findDicTextData(this.deviceStatusDicData, data.plcStatus);
            this.items[index].plcStatus = data.plcStatus;
        }
        if(data.masterCardStatus != null) {
            this.items[index].masterCardStatusName = findDicTextData(this.deviceStatusDicData, data.masterCardStatus);
            this.items[index].masterCardStatus = data.masterCardStatus;
        }

        if(data.slaveCardStatus != null) {
            this.items[index].slaveCardStatusName = findDicTextData(this.deviceStatusDicData, data.slaveCardStatus);
            this.items[index].slaveCardStatus = data.slaveCardStatus;
        }

        if(data.servo != null) {
            this.items[index].servoName = findDicTextData(this.servoStatusDicData, data.servo);
            this.items[index].servo = data.servo;
        }

        if(data.emergencyStop != null) {
            this.items[index].emergencyStopName = findDicTextData(this.stopStatusDicData, data.emergencyStop);
            this.items[index].emergencyStop = data.emergencyStop;
        }

        if(data.footWarning != null) {
            this.items[index].footWarningName = findDicTextData(this.footStatusDicData, data.footWarning);
            this.items[index].footWarning = data.footWarning;
        }


      },
      getDataFetchRefresh() { // customize data loading for table from server
          getApiManagerError().post(`${apiBaseUrl}/device-management/condition-monitoring/get-detail-by-id`,
          {
            guidList: this.guidList.join(),
          }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              for (let i = 0; i < data.length; i++) {
                let dataInfo = data[i];
                let index = this.findIndex(dataInfo.guid);
                if(index != -1) {
                    this.changeDetailData(index, dataInfo);
                }
              }
              //this.transformData(data);
              //this.getDataFetchRefresh();
              //this.isLoading = false;
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
        this.deviceCategoryOptions.push({value: null, text: this.$t('permission-management.all')});
      },
      siteData(newVal, oldVal) { // maybe called when the org data is loaded from server
          let nest = (newVal, id = 0, depth = 1) =>
              newVal
                  .filter(item => item.parentFieldId == id)
                  .map(item => ({
                      data: {fieldId: item.fieldId},
                      children: nest(newVal, item.fieldId, depth + 1),
                      id: id++,
                      state: {expanded: true},
                      text: item.fieldDesignation
                  }));
          let treeData = nest(newVal);

        let generateSpace = (count) => {
          let string = '';
          while (count--) {
            string += '&nbsp;&nbsp;&nbsp;&nbsp;';
          }
          return string;
        };

          this.siteSelectOptions = [];

        let changeFieldTree = (treeData, index) => {
            if (!treeData || treeData.length === 0) {
                return;
            }
            let tmp = treeData;
            for (let i = 0; i < tmp.length; i++) {
                changeFieldTree(tmp[i].children, index + 1);
                this.siteSelectOptions.unshift({
                    value: tmp[i].data.fieldId,
                    html: `${generateSpace(index)}${tmp[i].text}`
                });
            }
        };

          changeFieldTree(treeData, 1);
          this.siteSelectOptions.unshift({
              value: null,
              html: `${this.$t('permission-management.all')}`
          });

      },
    }
  }
</script>
