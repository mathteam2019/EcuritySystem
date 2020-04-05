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
                <div><span>{{$t('maintenance-management.process-task.hand')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.missing-report')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.missing-report-rate')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.mistakeReport}}</span>
                  <span v-else>0</span></div>
                <div><span>{{$t('statistics.evaluate-monitors.mistake-report')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>{{$t('statistics.evaluate-monitors.mistake-report-rate')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.artificial-judge')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.artificial-judge-missing')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.artificial-judge-missing-rate')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.artificialJudgeMistake}}</span>
                  <span v-else>0</span></div>
                <div><span>{{$t('statistics.evaluate-monitors.artificial-judge-mistake')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>{{$t('statistics.evaluate-monitors.artificial-judge-mistake-rate')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.intelligence-judge')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.intelligence-judge-missing')}}</span></div>
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
                <div><span>{{$t('statistics.evaluate-monitors.intelligence-judge-missing-rate')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div><span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.intelligenceJudgeMistake}}</span>
                  <span v-else>0</span></div>
                <div><span>{{$t('statistics.evaluate-monitors.intelligence-judge-mistake')}}</span></div>
              </div>
            </div>
          </b-card>
        </b-col>
        <b-col>
          <b-card class="no-padding" style="background-color: #fff;">
            <div class="statistics-item type-2">
              <div style="background-color: #009900;">
                <b-img src="/assets/img/export.svg"/>
              </div>
              <div>
                <div>
                  <span v-if="preViewData.totalStatistics==null">0%</span>
                  <span v-else-if="preViewData.totalStatistics.intelligenceJudge!==0">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                  <span v-else>0%</span>
                </div>
                <div><span>{{$t('statistics.evaluate-monitors.intelligence-judge-mistake-rate')}}</span></div>
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
                <h5>{{$t('maintenance-management.process-task.hand')}}</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.missingReport/preViewData.totalStatistics.total * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.missingReport/preViewData.totalStatistics.total * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="172" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.total!==0">{{Math.round(preViewData.totalStatistics.mistakeReport/preViewData.totalStatistics.total * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>{{$t('statistics.evaluate-monitors.missing-report')}}
                  {{$t('statistics.evaluate-monitors.mistake-report')}}</h5>
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
                <h5>{{$t('statistics.evaluate-monitors.artificial-judge')}}</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.artificialJudgeMissing/preViewData.totalStatistics.artificialJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics==null">0%</span>
                    <span class="chart percent clearfix" v-else-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMissing/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="172" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics.artificialJudge!==0">{{Math.round(preViewData.totalStatistics.artificialJudgeMistake/preViewData.totalStatistics.artificialJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>{{$t('statistics.evaluate-monitors.missing-report')}}
                  {{$t('statistics.evaluate-monitors.mistake-report')}}</h5>
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
                <h5>{{$t('statistics.evaluate-monitors.intelligence-judge')}}</h5>
              </b-card-header>
              <b-row style="height: 300px;">
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-1">
                  <radial-progress-bar v-if="preViewData.totalStatistics!=null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.intelligenceJudgeMissing/preViewData.totalStatistics.intelligenceJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix" v-if="preViewData.totalStatistics.intelligenceJudge!==0">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMissing/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                    <span class="chart percent clearfix" v-else>0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else :diameter="156" :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.missing-report')}}
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar v-if="preViewData.totalStatistics==null" :diameter="156" :strokeWidth="8"
                                       :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else-if="preViewData.totalStatistics.intelligenceJudge!==0" :diameter="172"
                                       :strokeWidth="8"
                                       :completed-steps="Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)"
                                       :total-steps=100>
                    <span class="chart percent clearfix">{{Math.round(preViewData.totalStatistics.intelligenceJudgeMistake/preViewData.totalStatistics.intelligenceJudge * 100)}}%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>
                  <radial-progress-bar v-else-if="preViewData.totalStatistics.intelligenceJudge===0" :diameter="172"
                                       :strokeWidth="8" :completed-steps="0" :total-steps=100>
                    <span class="chart percent clearfix">0%</span>
                    {{$t('statistics.evaluate-monitors.mistake-report')}}
                  </radial-progress-bar>

                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>
              <b-card-header>
                <h5>{{$t('statistics.evaluate-monitors.missing-report')}}
                  {{$t('statistics.evaluate-monitors.mistake-report')}}</h5>
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
                <h5 class="text-center my-4">{{$t('statistics.evaluate-monitors.table-title')}}</h5>
              </b-card-header>

              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('knowledge-base.site')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.fieldId === null">{{this.allField}}</span>
                  <span v-else>{{getSiteLabel(filter.fieldId)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('statistics.evaluate-monitors.security-device')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.deviceId === null">{{allDevice}}</span>
                  <span v-else>{{getDeviceLabel(filter.deviceId)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('statistics.view.operator-type')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.userName === null">{{$t('statistics.view.operator')}}</span>
                  <span v-else>{{filter.userName}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('statistics.view.operator')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.userName===null">{{$t('personal-inspection.all')}}</span>
                  <span v-else>{{filter.userName}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('maintenance-management.process-task.judge')}}:</b></b-col>
                <b-col cols="11"><span>{{this.getDateTimeFormat(filter.startTime)}}-{{this.getDateTimeFormat(filter.endTime)}}</span>
                </b-col>
              </b-row>
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('statistics.evaluate-monitors.step-size')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.statWidth==='hour'">{{$t('statistics.hour')}}</span>
                  <span v-else-if="filter.statWidth==='day'">{{$t('statistics.day')}}</span>
                  <span v-else-if="filter.statWidth==='week'">{{$t('statistics.week')}}</span>
                  <span v-else-if="filter.statWidth==='month'">{{$t('statistics.month')}}</span>
                  <span v-else-if="filter.statWidth==='quarter'">{{$t('statistics.quarter')}}</span>
                  <span v-else>{{$t('statistics.year')}}</span>
                </b-col>
              </b-row>

              <div class="table-wrapper table-responsive overflow-auto">
                <vuetable
                  ref="taskVuetable"
                  :api-url="taskVuetableItems.apiUrl"
                  :fields="taskVuetableItems.fields"
                  :http-fetch="taskVuetableHttpFetch"
                  :per-page="taskVuetableItems.perPage"
                  track-by="id"
                  pagination-path="pagination"
                  class="table-hover"
                  @vuetable:checkbox-toggled="onCheckStatusChange"
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

  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
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
      this.setPeriodLabel('hour');
    },
    data() {
      return {
        showTable: false,
        lineChart1Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: [this.$t('statistics.evaluate-monitors.missing-report'), this.$t('statistics.evaluate-monitors.mistake-report')],
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
              name: this.$t('statistics.evaluate-monitors.missing-report'),
              type: 'line',
              stack: '总量',
              symbolSize: 12,
              data: [0]
            },
            {
              name: this.$t('statistics.evaluate-monitors.mistake-report'),
              type: 'line',
              symbolSize: 12,
              stack: '总量',
              data: [0]
            }
          ],
          color: ['#ff0000', '#009900']
        },

        lineChart2Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: [this.$t('statistics.evaluate-monitors.missing-report'), this.$t('statistics.evaluate-monitors.mistake-report')],
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
              name: this.$t('statistics.evaluate-monitors.missing-report'),
              type: 'line',
              stack: this.$t('statistics.view.total'),
              symbolSize: 12,
              data: [0]
            },
            {
              name: this.$t('statistics.evaluate-monitors.mistake-report'),
              type: 'line',
              symbolSize: 12,
              stack: this.$t('statistics.view.total'),
              data: [0]
            }
          ],
          color: ['#ff0000', '#009900']
        },

        lineChart3Options: {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: [this.$t('statistics.evaluate-monitors.missing-report'), this.$t('statistics.evaluate-monitors.mistake-report')],
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
              name: this.$t('statistics.evaluate-monitors.missing-report'),
              type: 'line',
              stack: this.$t('statistics.view.total'),
              symbolSize: 12,
              data: [0]
            },
            {
              name: this.$t('statistics.evaluate-monitors.mistake-report'),
              type: 'line',
              symbolSize: 12,
              stack: this.$t('statistics.view.total'),
              data: [0]
            }
          ],
          color: ['#ff0000', '#009900']
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
        renderedCheckList: [],
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
        monthLabel: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
        xWeek: ['1', '2', '3', '4', '5'],
        xDay: [],
        xHour: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'],

        isExpanded: false,
        onSiteOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'way_1', text: "通道1"},
          {value: 'way_2', text: "通道2"},
          {value: 'way_3', text: "通道3"},
        ],
        onSiteOption: [],
        securityDeviceOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'security_device_1', text: "安检仪001"},
          {value: 'security_device_2', text: "安检仪002"},
          {value: 'security_device_3', text: "安检仪003"},
        ],
        operatorTypeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '引导员', text: "引导员"},
          {value: '判图员', text: "判图员"},
          {value: '手检员', text: "手检员"},
        ],
        statisticalStepSizeOptions: [
          {value: 'hour', text: this.$t('statistics.hour')},
          {value: 'day', text: this.$t('statistics.day')},
          {value: 'week', text: this.$t('statistics.week')},
          {value: 'month', text: this.$t('statistics.month')},
          {value: 'quarter', text: this.$t('statistics.quarter')},
          {value: 'year', text: this.$t('statistics.year')},
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

        periodLabel: '期间(時)',
        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/statistics/get-evaluatejudge-statistics`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('knowledge-base.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'time',
              title: this.setPeriodLabel,
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'total',
              title: this.$t('statistics.hand-checks.total'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'missingReport',
              title: this.$t('statistics.evaluate-monitors.missing-report-total'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'missingReportRate',
              title: this.$t('statistics.evaluate-monitors.missing-report-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (missingReportRate) => {
                if (missingReportRate == null) return '';
                return missingReportRate.toFixed(1);
              }
            },
            {
              name: 'mistakeReport',
              title: this.$t('statistics.evaluate-monitors.mistake-report-total'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'mistakeReportRate',
              title: this.$t('statistics.evaluate-monitors.mistake-report-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudge',
              title: this.$t('statistics.evaluate-monitors.artificial-judge-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMissing',
              title: this.$t('statistics.evaluate-monitors.artificial-judge-missing'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMissingRate',
              title: this.$t('statistics.evaluate-monitors.artificial-judge-missing-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (artificialJudgeMissingRate) => {
                if (artificialJudgeMissingRate == null) return '';
                return artificialJudgeMissingRate.toFixed(1);
              }
            },
            {
              name: 'artificialJudgeMistake',
              title: this.$t('statistics.evaluate-monitors.artificial-judge-mistake'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'artificialJudgeMistakeRate',
              title: this.$t('statistics.evaluate-monitors.artificial-judge-mistake-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (artificialJudgeMistakeRate) => {
                if (artificialJudgeMistakeRate == null) return '';
                return artificialJudgeMistakeRate.toFixed(1);
              }
            },
            {
              name: 'intelligenceJudge',
              title: this.$t('statistics.evaluate-monitors.intelligence-judge-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMissing',
              title: this.$t('statistics.evaluate-monitors.intelligence-judge-missing'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMissingRate',
              title: this.$t('statistics.evaluate-monitors.intelligence-judge-missing-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (intelligenceJudgeMissingRate) => {
                if (intelligenceJudgeMissingRate == null) return '';
                return intelligenceJudgeMissingRate.toFixed(1);
              }
            },
            {
              name: 'intelligenceJudgeMistake',
              title: this.$t('statistics.evaluate-monitors.intelligence-judge-mistake'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'intelligenceJudgeMistakeRate',
              title: this.$t('statistics.evaluate-monitors.intelligence-judge-mistake-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (intelligenceJudgeMistakeRate) => {
                if (intelligenceJudgeMistakeRate == null) return '';
                return intelligenceJudgeMistakeRate.toFixed(1);
              }
            }
          ],
          perPage: 10,
        }
      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
        this.changeCheckAllStatus();
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
      setPeriodLabel (newVal) {
        if(getLocale() === 'zh') {
          //this.periodLabel = '时间段';
          switch (newVal) {
            case 'hour':
              this.periodLabel = '时间段';
              break;
            case 'day':
              this.periodLabel = '日';
              break;
            case 'week':
              this.periodLabel = '周';
              break;
            case 'month':
              this.periodLabel = '月';
              break;
            case 'quarter':
              this.periodLabel = '季度';
              break;
            case 'year':
              this.periodLabel = '年';
              break;
          }
        }else{
          switch (newVal) {
            case 'hour':
              this.periodLabel = 'Periods(hour)';
              break;
            case 'day':
              this.periodLabel = 'Day';
              break;
            case 'week':
              this.periodLabel = 'Week';
              break;
            case 'month':
              this.periodLabel = 'Month';
              break;
            case 'quarter':
              this.periodLabel = 'Quarter';
              break;
            case 'year':
              this.periodLabel = 'Year';
              break;
          }
        }
        return this.periodLabel;

      },
      selectAll(value) {
        this.$refs.taskVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.taskVuetable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus() {
        let selectList = this.$refs.taskVuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNone();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAll(true);
            }
          }
        } else {
          this.selectNone();
        }

      },
      onCheckStatusChange(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatus();
        } else {
          this.selectNone();
        }
      },
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
        getApiManager().post(`${apiBaseUrl}/device-management/device-table/device/get-active-security`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              let options = [];
              options = data.map(opt => ({
                text: opt.deviceName ? opt.deviceName : "Unknown",
                value: opt.deviceId
              }));
              let allFieldStr = "";
              let cnt = data.length;

              allFieldStr = allFieldStr + data[0].deviceName;
              //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
              for (let i = 1; i < cnt; i++) {

                allFieldStr = allFieldStr + ", " + data[i].deviceName;

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
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : true,
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
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : true,
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
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 || this.showTable === false ? checkedAll : true,
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
        if(this.filter.startTime !== null && this.filter.endTime !== null) {

          if (this.filter.startTime >= this.filter.endTime) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`maintenance-management.process-task.time-select`), {
              duration: 3000,
              permanent: false
            });
            return;
          }

        }

        this.getPreviewData();
        this.setPeriodLabel(this.filter.statWidth);
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
        this.$refs.taskVuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
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
          temp.id = temp.time;
          this.renderedCheckList.push(data.detailedStatistics[j].time);

          if(this.filter.statWidth === 'hour') {
            if (temp.time < 9) {
              temp.time = '0' + temp.time + ' : 00 ~ 0' + (temp.time + 1) + ': 00';
            }
            else if(temp.time === 9){
              temp.time = '09 :00 ~ 10 : 00';
            }
            else {
              temp.time = temp.time + ' : 00 ~ ' + (temp.time + 1) + ': 00';
            }
          }
          if(this.filter.statWidth === 'day' && getLocale() === 'zh') {
            temp.time = temp.time + '日';
          }
          if(this.filter.statWidth === 'week' && getLocale() === 'zh') {
            temp.time = temp.time + '周';
          }
          if(this.filter.statWidth === 'month') {
            if(getLocale() === 'zh') {
              temp.time = temp.time + '月';
            }else {
              temp.time = this.monthLabel[temp.time-1];
            }
          }
          if(this.filter.statWidth === 'quarter') {
            temp.time = temp.time + this.$t('statistics.quarter');
          }
          if(this.filter.statWidth === 'year') {
            temp.time = temp.time +  this.$t('statistics.year');
          }
          transformed.data.push(temp);
        }

        return transformed

      },
      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckList = [];

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.taskVuetableItems.perPage,
          filter: this.filter,
        });
      },
    }
  }
</script>

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
        stroke: #009900 !important;

        &[stroke-dashoffset="0"] {
          stroke: #d7d7d7 !important;
        }
      }
    }

  }

</style>
