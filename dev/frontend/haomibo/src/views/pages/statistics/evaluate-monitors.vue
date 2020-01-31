<style lang="scss">

  .evaluate-monitors {

    span.cursor-p {
      cursor: pointer !important;
    }

    .rounded-span {
      width: 20px;
      height: 20px;
      border-radius: 10px;
      cursor: pointer;
      background-color: #007bff;
    }

    .no-padding {
      .card-body {
        padding: 0;
      }
    }

    .statistics-item {
      height: 9vw;
      display: flex;
      align-items: center;
      $padding-x: 35px;
      $padding-y: 20px;
      padding: $padding-y $padding-x;
      justify-content: stretch;

      & > div:nth-child(1) {
        $size: 40px;
        width: $size;
        height: $size;

        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 5px;

        margin-right: 20px;

      }

      & > div:nth-child(2) {
        display: flex;
        flex-direction: column;

        & > div:nth-child(1) {
          display: flex;

          span {
            font-size: 2rem;
            font-weight: bold;
          }
        }

        & > div:nth-child(2) {
          display: flex;

          span {
            font-size: 1rem;
          }
        }


      }

      &.type-1 {
        & > div:nth-child(1) {
          img {
            width: 100%;
            height: 100%;
          }
        }

        & > div:nth-child(2) {
          & > div:nth-child(1) {
            span {
              color: white;
            }
          }

          & > div:nth-child(2) {
            span {
              color: white;
            }
          }
        }
      }

      &.type-2 {
        & > div:nth-child(1) {

          img {
            $size: 50%;
            width: $size;
            height: $size;
          }
        }
      }

      & > div:nth-child(2) {
        & > div:nth-child(1) {
          span {
            color: black;
          }
        }

        & > div:nth-child(2) {
          span {
            color: #999999;
          }
        }
      }

    }

    .chart.percent {
      font-size: 2rem;
      font-weight: bold;
    }

    .chart-type-1 {

      .radial-progress-container circle {
        stroke: #ff0000 !important;

        &[stroke-dashoffset="0"] {
          stroke: #d7d7d7 !important;
        }
      }
    }

    .chart-type-2 {

      .radial-progress-container circle {
        stroke: #ff6600 !important;

        &[stroke-dashoffset="0"] {
          stroke: #d7d7d7 !important;
        }
      }
    }

  }

</style>

<template>
  <div class="evaluate-monitors">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <div class="h-100 d-flex flex-column">
      <b-row class="pt-2">
        <b-col cols="8">
          <b-row>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.on-site')">
                <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
              </b-form-group>
            </b-col>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.security-device')">
                <b-form-select v-model="filter.deviceId" :options="manualDeviceOptions" plain/>
              </b-form-group>
            </b-col>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.hand-checker')">
                <b-form-input v-model="filter.userName"/>
              </b-form-group>
            </b-col>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.time')">
                <b-form-select v-model="filter.statWidth" :options="statisticalStepSizeOptions" plain/>
              </b-form-group>
            </b-col>
            <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
            </b-col>
          </b-row>
        </b-col>
        <b-col cols="8" v-if="isExpanded">
          <b-row>
            <b-col>
              <b-form-group :label="$t('statistics.view.start-time')">
                <date-picker v-model="filter.startTime" type="datetime" format="YYYY-MM-DD HH:mm"
                             placeholder=""/>
              </b-form-group>
            </b-col>

            <b-col>
              <b-form-group :label="$t('statistics.view.end-time')">
                <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
                             placeholder=""/>


              </b-form-group>
            </b-col>

            <b-col/>
            <b-col/>
            <b-col/>
            <b-col/>


          </b-row>
        </b-col>
        <b-col cols="4" class="d-flex justify-content-end align-items-center">
          <div>
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
            </b-button>
          </div>
        </b-col>
      </b-row>
      <b-row class="mt-2">
        <b-col>
          <b-card class="no-padding" style="background-color: #365ae0;">
            <div class="statistics-item type-1">
              <div>
                <b-img src="/assets/img/hand_check_icon.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.total}}</span>
                  <span v-else>0</span>
                </div>
                <div><span>手检</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.missingReport}}</span>
                  <span v-else>0</span></div>
                <div><span>误报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.missingReport/preViewData.totalStatistics.total * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>误报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.mistakeReport}}</span>
                  <span v-else>0</span></div>
                <div><span>漏报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>漏报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
      </b-row>
      <b-row class="mt-4">
        <b-col>
          <b-card class="no-padding" style="background-color: #1989fa;">
            <div class="statistics-item type-1">
              <div>
                <b-img src="/assets/img/hand_check_icon.svg"/>
              </div>
              <div>
                <div><span
                  v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.artificialJudge}}</span>
                  <span v-else>0</span></div>
                <div><span>手检（人工判图）</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.artificialJudgeMissing}}</span>
                  <span v-else>0</span></div>
                <div><span>人工判图误报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMissing/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>人工判图误报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.artificialJudgeMistake}}</span>
                  <span v-else>0</span></div>
                <div><span>人工判图漏报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>人工判图漏报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
      </b-row>
      <b-row class="mt-4">
        <b-col>
          <b-card class="no-padding" style="background-color: #0cb4d2;">
            <div class="statistics-item type-1">
              <div>
                <b-img src="/assets/img/hand_check_icon.svg"/>
              </div>
              <div>
                <div><span
                  v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.intelligenceJudge}}</span>
                  <span v-else>0</span></div>
                <div><span>手检（智能判图）</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.intelligenceJudgeMissing}}</span>
                  <span v-else>0</span></div>
                <div><span>智能判图误报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: red;">
                <b-img src="/assets/img/circle_close.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.intelligenceJudge!==0">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMissing/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>智能判图误报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.intelligenceJudgeMistake}}</span>
                  <span v-else>0</span></div>
                <div><span>智能判图漏报</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #ff6600;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.intelligenceJudge!==0">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>智能判图漏报率</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
      </b-row>
      <b-row class="pt-2 mt-4">
        <b-col cols="8">
        </b-col>
        <b-col cols="4" class="d-flex justify-content-end align-items-center">
          <div>
            <b-button size="sm" class="ml-2" variant="info default" @click="showTable = !showTable">
              <i class="icofont-exchange"/>&nbsp;{{ $t('statistics.evaluate-monitors.displacement') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default"
                      :disabled="checkPermItem('evaluate_statistics_export')" @click="onExportButton()">
              <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default"
                      :disabled="checkPermItem('evaluate_statistics_print')" @click="onPrintButton()">
              <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
            </b-button>
          </div>
        </b-col>
      </b-row>
      <div v-if="!showTable">
        <b-row class="mt-3">
          <b-col class="pr-0" cols="4">
            <b-card>
              <b-card-header>
                <h5>手检</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.missingReport/preViewData.totalStatistics.total * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.missingReport/preViewData.totalStatistics.total * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    误报
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="172" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    漏报
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    漏报
                  </radial-progress-bar>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>误报 漏报</h5>
              </b-card-header>
              <b-row>
                <b-col>
                  <v-chart :options="lineChart1Options" style="width: 100%; height: 300px" :autoresize="true"/>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
        <b-row class="mt-3">
          <b-col class="pr-0" cols="4">
            <b-card>
              <b-card-header>
                <h5>手检（人工判图）</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.artificialJudgeMissing/preViewData.totalStatistics.artificialJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMissing/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    误报
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="172" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    漏报
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    漏报
                  </radial-progress-bar>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>误报 漏报</h5>
              </b-card-header>
              <b-row>
                <b-col>
                  <v-chart :options="lineChart2Options" style="width: 100%; height: 300px" :autoresize="true"/>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
        <b-row class="mt-3">
          <b-col class="pr-0" cols="4">
            <b-card>
              <b-card-header>
                <h5>手检（智能判图）</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.intelligenceJudgeMissing/preViewData.totalStatistics.intelligenceJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics.intelligenceJudge!==0">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMissing/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    误报
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics==null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    漏报
                  </radial-progress-bar>
                  <radial-progress-bar v-else-if="preViewData.totalStatistics.intelligenceJudge!==0" :diameter="172"
                                       :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                    漏报
                  </radial-progress-bar>
                  <radial-progress-bar v-else-if="preViewData.totalStatistics.intelligenceJudge===0" :diameter="172"
                                       :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    漏报
                  </radial-progress-bar>

                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>误报 漏报</h5>
              </b-card-header>
              <b-row>
                <b-col>
                  <v-chart :options="lineChart3Options" style="width: 100%; height: 300px" :autoresize="true"/>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
      </div>
      <div v-if="showTable">
        <b-row class="mt-3">
          <b-col>
            <b-card>
              <b-card-header>
                <h5 class="text-center my-4">毫米波人体查验评价判图统计</h5>
              </b-card-header>

              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>现场:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.fieldId === null">{{this.allField}}</span>
                  <span v-else>{{getSiteLabel(filter.fieldId)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>安检仪:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.deviceId === null">{{allDevice}}</span>
                  <span v-else>{{getDeviceLabel(filter.deviceId)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>操作员类型:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.userName === null">手检员</span>
                  <span v-else>{{filter.userName}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>操作员:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.userName===null">全部</span>
                  <span v-else>{{filter.userName}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>时间:</b></b-col>
                <b-col cols="11"><span>{{this.getDateTimeFormat(filter.startTime)}}-{{this.getDateTimeFormat(filter.endTime)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>统计步长:</b></b-col>
                <b-col cols="11"><span v-if="filter.statWidth==='hour'">时</span>
                  <span v-else-if="filter.statWidth==='day'">天</span>
                  <span v-else-if="filter.statWidth==='week'">周</span>
                  <span v-else-if="filter.statWidth==='month'">月</span>
                  <span v-else-if="filter.statWidth==='quarter'">季度</span>
                  <span v-else>年</span></b-col>
              </b-row>

              <div class="table-wrapper table-responsive overflow-auto">
                <vuetable
                  ref="taskVuetable"
                  :api-url="taskVuetableItems.apiUrl"
                  :fields="taskVuetableItems.fields"
                  :http-fetch="taskVuetableHttpFetch"
                  :per-page="taskVuetableItems.perPage"
                  track-by="time"
                  pagination-path="pagination"
                  class="table-hover"
                  @vuetable:pagination-data="onTaskVuetablePaginationData"
                >
                </vuetable>
              </div>
              <div class="pagination-wrapper">
                <vuetable-pagination-bootstrap
                  ref="taskVuetablePagination"
                  @vuetable-pagination:change-page="onTaskVuetableChangePage"
                  :initial-per-page="taskVuetableItems.perPage"
                  @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
                />
              </div>
            </b-card>
          </b-col>
        </b-row>
      </div>
      <b-row class="mt-3"/>
    </div>
    <b-modal centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export')
            }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExport()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
  </div>
</template>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap"
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import Switches from 'vue-switches'
  import RadialProgressBar from 'vue-radial-progress'
  import ECharts from 'vue-echarts'
  import 'echarts/lib/chart/line'
  import 'echarts/lib/component/legend'
  import 'echarts/lib/component/tooltip'
  import _ from 'lodash'
  import {responseMessages} from "../../../constants/response-messages";
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {getApiManager, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer} from '../../../api';
  import vSelect from 'vue-select';
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  import {checkPermissionItem, getDirection} from "../../../utils";
  import {validationMixin} from "vuelidate";

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'radial-progress-bar': RadialProgressBar,
      'v-chart': ECharts,
      'date-picker': DatePicker,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },
    mounted() {

      this.getSiteOption();
      this.getManualDeviceData();
      this.getPreviewData();
    },
    data() {
      return {
        showTable: false,
        lineChart1Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['误报', '漏报'],
            right: 30,
            icon: 'circle'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: []
          },
          yAxis: {
            type: 'value',
            axisLine: {
              show: false
            }
          },
          series: [
            {
              name: '误报',
              type: 'line',
              stack: '总量',
              symbolSize: 12,
              data: [0]
            },
            {
              name: '漏报',
              type: 'line',
              symbolSize: 12,
              stack: '总量',
              data: [0]
            }
          ],
          color: ['#ff0000', '#ff6600']
        },

        lineChart2Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['误报', '漏报'],
            right: 30,
            icon: 'circle'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: []
          },
          yAxis: {
            type: 'value',
            axisLine: {
              show: false
            }
          },
          series: [
            {
              name: '误报',
              type: 'line',
              stack: '总量',
              symbolSize: 12,
              data: [0]
            },
            {
              name: '漏报',
              type: 'line',
              symbolSize: 12,
              stack: '总量',
              data: [0]
            }
          ],
          color: ['#ff0000', '#ff6600']
        },

        lineChart3Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['误报', '漏报'],
            right: 30,
            icon: 'circle'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: []
          },
          yAxis: {
            type: 'value',
            axisLine: {
              show: false
            }
          },
          series: [
            {
              name: '误报',
              type: 'line',
              stack: '总量',
              symbolSize: 12,
              data: [0]
            },
            {
              name: '漏报',
              type: 'line',
              symbolSize: 12,
              stack: '总量',
              data: [0]
            }
          ],
          color: ['#ff0000', '#ff6600']
        },

        filter: {
          fieldId: null,
          deviceId: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },
        siteData: [],
        allField: '',
        allDevice: '',
        preViewData: [],
        manualDeviceOptions: [],

        link: '',
        params: {},
        name: '',
        fileSelection: [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,

        xYear: [],
        xQuarter: ['1', '2', '3', '4'],
        xMonth: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        xWeek: ['1', '2', '3', '4', '5'],
        xDay: [],
        xHour: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'],

        isExpanded: false,
        onSiteOptions: [
          {value: null, text: "全部"},
          {value: 'way_1', text: "通道1"},
          {value: 'way_2', text: "通道2"},
          {value: 'way_3', text: "通道3"},
        ],
        onSiteOption: [],
        securityDeviceOptions: [
          {value: null, text: "全部"},
          {value: 'security_device_1', text: "安检仪001"},
          {value: 'security_device_2', text: "安检仪002"},
          {value: 'security_device_3', text: "安检仪003"},
        ],
        operatorTypeOptions: [
          {value: null, text: "全部"},
          {value: '引导员', text: "引导员"},
          {value: '判图员', text: "判图员"},
          {value: '手检员', text: "手检员"},
        ],
        statisticalStepSizeOptions: [
          {value: 'hour', text: "时"},
          {value: 'day', text: "天"},
          {value: 'week', text: "周"},
          {value: 'month', text: "月"},
          {value: 'quarter', text: "季度"},
          {value: 'year', text: "年"},
        ],
        // TODO: refactor temp table data to api mode
        tempData: {
          data: [1, 2, 3, 4, 5].map((e) => {
            let statusSet = [
              "pending_dispatch",
              "pending_review",
              "while_review",
              "pending_inspection",
              "while_inspection"
            ];

            return {
              id: e,
              taskNumber: 'HR201909210001',
              // operationMode: 'HR201909210001',
              status: _.sample(statusSet),
              onSite: '',
              securityInstrument: '',
              guide: '张怡宁',
              scanStartTime: '2019-10-23.10:30',
              scanEndTime: '2019-10-23.10:30',
              judgementStation: '丹东站',
              judge: '张怡宁',
              judgementStartTime: '2019-10-23.10:30',
              judgementEndTime: '2019-10-23.10:30',
              handCheckStation: '丹东站',
              handChecker: '张怡宁',
              handCheckStartTime: '2019-10-23.10:30'

            }
          }),
          pagination: {
            total: 5,
            per_page: 5,
            current_page: 1,
            last_page: 1,
            from: 1,
            to: 5
          }
        },
        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/statistics/get-evaluatejudge-statistics`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'time',
              title: '序号',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (time) => {
                if (this.filter.statWidth === 'hour') return time + 1;
                else return time;
              }
            },
            {
              name: 'time',
              title: '时间段',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'total',
              title: '手检总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'missingReport',
              title: '误报总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'missingReportRate',
              title: '误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'mistakeReport',
              title: '漏报总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'mistakeReportRate',
              title: '漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudge',
              title: '手检（人工判图）量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMissing',
              title: '人工判图误报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMissingRate',
              title: '人工判图误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMistake',
              title: '人工判图漏报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMistakeRate',
              title: '人工判图漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudge',
              title: '手检（智能判图）量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMissing',
              title: '智能判图误报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMissingRate',
              title: '智能判图误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMistake',
              title: '智能判图漏报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMistakeRate',
              title: '智能判图漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage: 10,
        }
      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
      },
      'operatingLogTableItems.perPage': function (newVal) {
        this.$refs.operatingLogTable.refresh();
      },
      siteData: function (newVal, oldVal) {

        this.onSiteOption = [];
        this.onSiteOption = newVal.map(site => ({
          text: site.fieldDesignation,
          value: site.fieldId
        }));
        this.onSiteOption.push({
          text: this.$t('personal-inspection.all'),
          value: null
        });
        if (this.onSiteOption.length === 0)
          this.onSiteOption.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      }
    },
    methods: {
      // showModal() {
      //   let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
      //   let checkedIds = this.$refs.taskVuetable.selectedTo;
      //   this.params = {
      //     'isAll': checkedIds.length > 0 ? checkedAll : true,
      //     'filter': this.filter,
      //     'idList': checkedIds.join()
      //   };
      //   this.link = `task/invalid-task/generate`;
      //   this.name = 'Invalid-Task';
      //   this.isModalVisible = true;
      // },
      closeModal() {
        this.isModalVisible = false;
      },
      getSiteLabel(value) {
        if (value === null || this.onSiteOption === null) return "";
        else {
          for (let i = 0; i < this.onSiteOption.length; i++) {
            if (this.onSiteOption[i].value === value)
              return this.onSiteOption[i].text;
          }
        }
      },
      getDeviceLabel(value) {
        if (value === null || this.manualDeviceOptions === null) return "";
        else {
          for (let i = 0; i < this.manualDeviceOptions.length; i++) {
            if (this.manualDeviceOptions[i].value === value)
              return this.manualDeviceOptions[i].text;
          }
        }
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManualDeviceData() {
        getApiManager().post(`${apiBaseUrl}/device-management/device-config/manual-device/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              let options = [];
              options = data.map(opt => ({
                text: opt.device ? opt.device.deviceName : "Unknown",
                value: opt.manualDeviceId
              }));
              let allFieldStr = "";
              let cnt = data.length;

              allFieldStr = allFieldStr + data[0].device.deviceName;
              //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
              for (let i = 1; i < cnt; i++) {

                allFieldStr = allFieldStr + ", " + data[i].device.deviceName;

              }
              this.allDevice = allFieldStr;

              this.manualDeviceOptions = options;
              this.manualDeviceOptions.push({
                text: this.$t('personal-inspection.all'),
                value: null
              });
              break;
          }
        });
      },
      getDateTimeFormat(dataTime) {
        if (dataTime == null) return '';
        return getDateTimeWithFormat(dataTime);
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll, checkedIds;
        if (this.showTable === false) {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        this.params = {
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : false,
          'filter': {'filter': this.filter},
          'idList': this.showTable === false ? checkedIds : checkedIds.join()
        };
        this.link = `task/statistics/evaluatejudge/generate`;
        this.name = 'Statistics-Evaluate';
        this.isModalVisible = true;
      },
      onExport() {
        let checkedAll, checkedIds;
        if (this.showTable === false) {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : false,
          'filter': {'filter': this.filter},
          'idList': this.showTable === false ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/evaluatejudge/generate`;
        if (this.showTable !== false && checkedIds.length === 0) {

        } else {
          downLoadFileFromServer(link, params, 'Statistics-Evaluate', this.fileSelection);
          this.hideModal('model-export')
        }
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onPrintButton() {
        let checkedAll, checkedIds;
        if (this.showTable === false) {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : false,
          'filter': {'filter': this.filter},
          'idList': this.showTable === false ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/evaluatejudge/generate`;
        if (this.showTable !== false && checkedIds.length === 0) {

        } else {
          printFileFromServer(link, params);
        }

      },


      getSiteOption() {
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
          let allFieldStr = "";
          let cnt = this.siteData.length;

          allFieldStr = allFieldStr + this.siteData[0].fieldDesignation;

          for (let i = 1; i < cnt; i++) {

            allFieldStr = allFieldStr + ", " + this.siteData[i].fieldDesignation;

          }
          this.allField = allFieldStr;
        })
          .catch((error) => {
          });
      },

      getPreviewData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-evaluatejudge-statistics`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;

          // if (this.filter.statWidth === 'year') {
          //   this.bar3ChartOptions.xAxis.data = this.xHour;
          // } else {
          this.xDay = Object.keys(this.preViewData.detailedStatistics);
          this.lineChart1Options.xAxis.data = this.xDay;
          this.lineChart2Options.xAxis.data = this.xDay;
          this.lineChart3Options.xAxis.data = this.xDay;

          for (let i = 0; i < this.xDay.length; i++) {
            let key = this.xDay[i];

            if (this.preViewData.detailedStatistics[i] != null) {
              this.lineChart1Options.series[0].data[i] = this.preViewData.detailedStatistics[key].missingReport;
              this.lineChart1Options.series[1].data[i] = this.preViewData.detailedStatistics[key].mistakeReport;
              this.lineChart2Options.series[0].data[i] = this.preViewData.detailedStatistics[key].artificialJudgeMissing;
              this.lineChart2Options.series[1].data[i] = this.preViewData.detailedStatistics[key].artificialJudgeMistake;
              this.lineChart3Options.series[0].data[i] = this.preViewData.detailedStatistics[key].intelligenceJudgeMissing;
              this.lineChart3Options.series[1].data[i] = this.preViewData.detailedStatistics[key].intelligenceJudgeMistake;

            }
          }
          //}
        });
      },

      onSearchButton() {

        this.getPreviewData();
        this.$refs.taskVuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          fieldId: null,
          deviceId: null,
          userName: null,
          statWidth: 'hour',
          startTime: null,
          endTime: null
        };
        //this.getPreviewData();
        //this.$refs.taskVuetable.refresh();

      },

      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page)
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
      },

      onDisplaceButton() {
        if (this.pageStatus1 === 'charts') {
          this.pageStatus1 = 'table';
        } else {
          this.pageStatus1 = 'charts';
        }
      },

      transform(response) {

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


        transformed.tKey = Object.keys(data.detailedStatistics);
        transformed.data = [];
        let temp;
        for (let i = 1; i <= Object.keys(data.detailedStatistics).length; i++) {
          let j = transformed.tKey[i - 1];

          temp = data.detailedStatistics[j];
          transformed.data.push(temp)
        }

        return transformed

      },
      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.taskVuetableItems.perPage,
          filter: this.filter,
        });
      },
    }
  }
</script>
