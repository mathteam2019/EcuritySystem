<template>
  <div class="hand-checks">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-row class="pt-2">
      <b-col cols="8">
        <b-row>

          <b-col>
            <b-form-group :label="$t('statistics.view.on-site')">
              <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.security-device')">
              <b-form-select v-model="filter.deviceId" :options="manualDeviceOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.operator')">
              <b-form-input v-model="filter.userName"/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.step-size')">
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

    <b-row class="parameter-items">
      <b-col>
        <b-card class="no-padding w-100 h-100" style="background-color: #1989fa;">
          <div class="statistics-item type-3">
            <div style="">
              <b-img src="/assets/img/scan.svg"/>
            </div>
            <div>
              <span class="font-weight-bold" v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.total}}</span>
              <span v-else>0</span>
            </div>
            <div><span>{{$t('maintenance-management.process-task.hand')}}</span></div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-row class="mb-4">
          <b-col>
            <b-card class="no-padding">
              <div class="statistics-item type-2">
                <div style="background-color: #009900">
                  <b-img src="/assets/img/glass_delete_icon.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.noSeizure}}</span>
                    <span v-else>0</span>
                  </div>

                  <div><span>{{$t('knowledge-base.no-seized')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding">
              <div class="statistics-item type-2">
                <div style="background-color: #009900">
                  <b-img src="/assets/img/glass_delete_icon.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.noSeizureRate)}}%</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.no-seizure-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #ff0000;">
                  <b-img src="/assets/img/glass_check_icon.svg"/>
                </div>
                <div>

                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.seizure}}</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('knowledge-base.seized')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #ff0000;">
                  <b-img src="/assets/img/glass_check_icon.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.seizureRate)}}%</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.seizure-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #344bf3;">
                  <b-img src="/assets/img/clock.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{(preViewData.totalStatistics.avgDuration-preViewData.totalStatistics.avgDuration%60)/60}}m{{Math.round(preViewData.totalStatistics.avgDuration)%60}}s</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.hand-checks.avg')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #19b8fa;">
                  <b-img src="/assets/img/up_arrow.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{(preViewData.totalStatistics.maxDuration-preViewData.totalStatistics.maxDuration%60)/60}}m{{preViewData.totalStatistics.maxDuration%60}}s</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.hand-checks.max')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #00bbb0;">
                  <b-img src="/assets/img/down_arrow.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{(preViewData.totalStatistics.minDuration-preViewData.totalStatistics.minDuration%60)/60}}m{{preViewData.totalStatistics.minDuration%60}}s</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.hand-checks.min')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
        </b-row>
      </b-col>

    </b-row>
    <b-row class="mt-4 mb-2">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton1()">
            <i class="icofont-exchange"/>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('hand_statistics_export')" @click="onExportButton('hand')">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('hand_statistics_print')" @click="onPrintButton()">
            <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>
    <b-row class="mt-3">
      <b-col v-if="pageStatus1==='charts'" class="charts-part-1">
        <b-row>
          <b-col cols="4">
            <b-card>
              <div class="w-100 flex-grow-1 d-flex flex-column justify-content-around">

                <div class="d-flex align-items-center justify-content-around">
                  <div class="pie-chart">
                    <v-chart :options="pieChart2Options" style="height: 300px" :autoresize="true"/>
                  </div>
                  <div class="legend-group part-2">
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('knowledge-base.seized')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">
                        {{preViewData.totalStatistics.seizure}}
                      </div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('knowledge-base.no-seized')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">
                        {{preViewData.totalStatistics.noSeizure}}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>

              <b-card-header>
                <h5>{{$t('maintenance-management.process-task.judge')}}</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div class="bar-chart-1-and-2">
                  <v-chart :options="barChart2Options" style="height: 300px" :autoresize="true"/>
                </div>
              </div>

            </b-card>
          </b-col>
        </b-row>
      </b-col>
      <b-col v-if="pageStatus1==='table'">
        <b-card>
          <b-card-header>

            <h5 class="text-center my-4">{{$t('statistics.view.table-title')}}</h5>

          </b-card-header>

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
    <b-row class="mt-4 mb-2">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton2()">
            <i class="icofont-exchange"/>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('hand_statistics_export')" @click="onExportButton('statistics')">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('hand_statistics_print')" @click="onPrintButton2()">
            <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>
    <b-row class="bottom-part mt-3 mb-3">
      <b-col v-show="pageStatus2==='charts'" class="charts-part-2">
        <b-row>
          <b-col cols="4">
            <b-card>
              <!--              <highcharts :constructor-type="'spline'" :options="wordCloudChartOptions"/>-->
              <div id="wordCloudy">
                <!--              <b-img src="/assets/img/brand.png" class="w-100 h-100"-->
                <!--                     style="object-fit: contain; object-position: center"/>-->
              </div>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>

              <b-card-header>
                <h5>{{$t('knowledge-base.seized')}}</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div class="bar-3-chart">
                  <v-chart :options="bar3ChartOptions" style="height: 300px; width: 100%" :autoresize="true"/>
                </div>
              </div>

            </b-card>
          </b-col>
        </b-row>
      </b-col>
      <b-col v-show="pageStatus2==='table'">
        <b-card>
          <b-card-header>
            <h5 class="text-center my-4">{{$t('statistics.view.table-title')}}</h5>
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
            <b-col cols="11"><span>引导员</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.view.operator')}}:</b></b-col>
            <b-col cols="11">
              <span v-if="filter.userName===null">{{$t('personal-inspection.all')}}</span>
              <span v-else>{{filter.userName}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.evaluate-monitors.time')}}:</b></b-col>
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

          <div class="table-wrapper table-responsive">
            <vuetable
              ref="taskVuetable2"
              :api-url="taskVuetable2Items.apiUrl"
              :fields="taskVuetable2Items.fields"
              :http-fetch="taskVuetable2HttpFetch"
              :per-page="taskVuetable2Items.perPage"
              track-by="time"
              pagination-path="pagination"
              class="table-hover"
              @vuetable:checkbox-toggled="onCheckStatusChange2"
              @vuetable:pagination-data="onTaskVuetable2PaginationData"
            >
            </vuetable>
          </div>
          <div class="pagination-wrapper">
            <vuetable-pagination-bootstrap
              ref="taskVuetable2Pagination"
              @vuetable-pagination:change-page="onTaskVuetable2ChangePage"
              :initial-per-page="taskVuetable2Items.perPage"
              @onUpdatePerPage="taskVuetable2Items.perPage = Number($event)"
            />
          </div>
        </b-card>
      </b-col>
    </b-row>
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
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import Switches from 'vue-switches';
  import ECharts from 'vue-echarts'
  import 'echarts/lib/chart/pie';
  import 'echarts/lib/chart/bar';
  import 'echarts/lib/component/tooltip';
  import 'echarts/lib/component/legend';
  import {responseMessages} from "../../../constants/response-messages";
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {getApiManager, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer} from '../../../api';
  import vSelect from 'vue-select';
  import 'vue-select/dist/vue-select.css'
  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
  import highcharts from "../../../data/highcharts";
  import wordcloud from "../../../data/wordcloud"

  //import  * as Highcharts from 'highcharts/highstock';
  //import * as Exporting from 'highcharts/modules/exporting';

  //const exporting = Exporting(Highcharts);
  //import HighchartsMore from 'highcharts/highcharts-more';
  // const Highcharts = require('highcharts');
  // import Vue from 'vue'
  // import HighchartsVue from 'highcharts-vue'
  //
  // Vue.use(HighchartsVue, {
  //   tagName: 'charts'
  // })
  // import {Chart} from 'highcharts-vue'
  import {validationMixin} from "vuelidate";
  import Modal from '../../../components/Modal/modal'

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
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
      this.getGraphData();
      this.setPeriodLabel('hour');
    },
    data() {

      return {
        isExpanded: false,

        wordCloudChartOptions: {
          series: [{
            type: 'wordcloud',
            data: []
          }],
          title: {
            text: this.$t('statistics.hand-checks.word-cloud')
          }
        },

        pieChart2Options: {
          tooltip: {
            trigger: 'item',
            formatter: `
<div style='position: relative'>
<div style='position: absolute;
    left: -8px;
    top: 50%;
    transform: translateY(-50%);
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
    border-right:8px solid #cccccc;'></div>
<div style='background-color: #cccccc; color: #303133; padding: 4px 8px; border-radius: 4px;'>{b}:{c}&nbsp;&nbsp;&nbsp;<span style='color:#1989fa'>{d}%</span></div>
</div>
`,
            backgroundColor: 'rgba(0,0,0,0)',
            transitionDuration: 0,
            position: function (point, params, dom, rect, size) {
              // fixed at top
              return [point[0] + 8, point[1] + 8];
            }
          },
          color: [
            '#ff0000',
            '#009900'
          ],
          series: [
            {
              type: 'pie',
              hoverAnimation: false,
              radius: ['80%', '90%'],
              avoidLabelOverlap: false,
              label: {
                normal: {
                  show: false,
                  position: 'outside',
                },
              },
              labelLine: {
                show: false,
                length: -34,
                length2: -30
              },
              data: [
                {value: 0, name: this.$t('knowledge-base.seized')},
                {value: 0, name: this.$t('knowledge-base.no-seized')}
              ]
            },

          ]
        },

        barChart2Options: {

          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: [this.$t('knowledge-base.no-seized'), this.$t('knowledge-base.seized')],
            icon: 'rect',
            right: 25
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: [],
            axisLine: {
              show: true
            },
            axisTick: {
              show: false
            }

          },
          yAxis: {
            type: 'value',
            splitLine: {
              show: true
            },
            axisLabel: {
              show: true,
              interval: 100
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: false
            }
          },
          color: ['#009900', '#ff0000'],

          series: [
            {
              name: this.$t('knowledge-base.no-seized'),
              type: 'bar',
              data: [0],
              barGap: '0%',
            },
            {
              name: this.$t('knowledge-base.seized'),
              type: 'bar',
              data: [0],
              barGap: '0%',
            },
          ]
        },
        bar3ChartOptions: {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: [this.$t('knowledge-base.seized')],
            icon: 'rect',
            right: 25
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: [],
            axisLine: {
              show: true
            },
            axisTick: {
              show: false
            }

          },
          yAxis: {
            type: 'value',
            splitLine: {
              show: true
            },
            axisLabel: {
              show: true,
              interval: 100
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: false
            }
          },
          color: ['#1989fa'],
          series: [
            {
              name: this.$t('knowledge-base.seized'),
              type: 'bar',
              barWidth: '30%',
              data: []
            }
          ]
        },

        pageStatus1: 'charts',
        link: '',
        params: {},
        name: '',
        pageStatus2: 'charts',
        fileSelection: [],
        renderedCheckList: [],
        renderedCheckList2: [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        button: 'hand',

        filter: {
          fieldId: null,
          deviceId: null,
          userCategory: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },

        isCheckAll: false,
        isCheckAll2: false,
        siteData: [],
        allField: '',
        allDevice: '',
        preViewData: [],
        manualDeviceOptions: [],
        wordCloudData: [],

        xYear: [],
        xQuarter: ['1', '2', '3', '4'],
        xMonth: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        xWeek: ['1', '2', '3', '4', '5'],
        xDay: [],
        xHour: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'],

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

        periodLabel : '期间(時)',

        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/statistics/get-handexamination-statistics`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('personal-inspection.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'time',
              title : this.setPeriodLabel,
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'total',
              title:  this.$t('statistics.hand-checks.total'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'noSeizure',
              title: this.$t('statistics.view.no-seizure'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'noSeizureRate',
              title: this.$t('statistics.view.no-seizure-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (noSeizureRate) => {
                if (noSeizureRate == null) return '';
                return noSeizureRate.toFixed(1);
              }
            },
            {
              name: 'seizure',
              title: this.$t('statistics.view.seizure'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'seizureRate',
              title: this.$t('statistics.view.seizure-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (seizureRate) => {
                if (seizureRate == null) return '';
                return seizureRate.toFixed(1);
              }
            },
            {
              name: 'avgDuration',
              title: this.$t('statistics.hand-checks.avg'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'maxDuration',
              title: this.$t('statistics.hand-checks.max'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'minDuration',
              title: this.$t('statistics.hand-checks.min'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage: 10,
        },

        taskVuetable2Items: {
          apiUrl: `${apiBaseUrl}/task/statistics/get-suspicionhandgoods-statistics`,
          fields: [

            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('personal-inspection.serial-number'),
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
              name: '1000001601',
              title: '安眠药',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '1000001602',
              title: '仿真枪',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '1000001603',
              title: '玩具枪',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '1000001604',
              title: '气枪',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '1000001605',
              title: '打火机',
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
        this.changeCheckAllStatus();
      },
      'taskVuetable2Items.perPage': function (newVal) {
        this.$refs.taskVuetable2.refresh();
        this.changeCheckAllStatus2();
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
          switch (newVal) {
            case 'hour':
              this.periodLabel = '期间(時)';
              break;
            case 'day':
              this.periodLabel = '期间(天)';
              break;
            case 'week':
              this.periodLabel = '期间(周)';
              break;
            case 'month':
              this.periodLabel = '期间(月)';
              break;
            case 'quarter':
              this.periodLabel = '期间(季度)';
              break;
            case 'year':
              this.periodLabel = '期间(年)';
              break;
          }
        }else{
          switch (newVal) {
            case 'hour':
              this.periodLabel = 'periods(hour)';
              break;
            case 'day':
              this.periodLabel = 'periods(day)';
              break;
            case 'week':
              this.periodLabel = 'periods(week)';
              break;
            case 'month':
              this.periodLabel = 'periods(month)';
              break;
            case 'quarter':
              this.periodLabel = 'periods(quarter)';
              break;
            case 'year':
              this.periodLabel = 'periods(year)';
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
      selectAll2(value) {
        this.$refs.taskVuetable2.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.taskVuetable2.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable2.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone2() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable2.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus2() {
        let selectList = this.$refs.taskVuetable2.selectedTo;
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
              this.selectNone2();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAll2(true);
            }
          }
        } else {
          this.selectNone2();
        }

      },
      onCheckStatusChange2(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatus2();
        } else {
          this.selectNone2();
        }
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      drawWordCloud() {
        var text = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean bibendum erat ac justo sollicitudin, quis lacinia ligula fringilla. Pellentesque hendrerit, nisi vitae posuere condimentum, lectus urna accumsan libero, rutrum commodo mi lacus pretium erat. Phasellus pretium ultrices mi sed semper. Praesent ut tristique magna. Donec nisl tellus, sagittis ut tempus sit amet, consectetur eget erat. Sed ornare gravida lacinia. Curabitur iaculis metus purus, eget pretium est laoreet ut. Quisque tristique augue ac eros malesuada, vitae facilisis mauris sollicitudin. Mauris ac molestie nulla, vitae facilisis quam. Curabitur placerat ornare sem, in mattis purus posuere eget. Praesent non condimentum odio. Nunc aliquet, odio nec auctor congue, sapien justo dictum massa, nec fermentum massa sapien non tellus. Praesent luctus eros et nunc pretium hendrerit. In consequat et eros nec interdum. Ut neque dui, maximus id elit ac, consequat pretium tellus. Nullam vel accumsan lorem.';

        var data = text.split(/[,\. ]+/g)
          .reduce(function (arr, word) {
            var obj = arr.find(function (obj) {
              return obj.name === word;
            });
            if (obj) {
              obj.weight += 1;
            } else {
              obj = {
                name: word,
                weight: 1
              };
              arr.push(obj);
            }
            return arr;
          }, []);

        this.wordCloudChartOptions.series.data = this.wordCloudData;
        Highcharts.chart('wordCloudy', {
          series: [{
            type: 'wordcloud',
            data: this.wordCloudData
          }],
          title: {
            text: this.$t('statistics.hand-checks.word-cloud')
          }
        });
      },

      getDataCodeValue(value) {
        const dictionary = {
          "1000001601": `安眠药`,
          "1000001602": `仿真枪`,
          "1000001603": `玩具枪`,
          "1000001604": `气枪`,
          "1000001605": `打火机`,
        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
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
      getCategoryLabel(value) {
        if (value === null || this.operatorTypeOptions === null) return "";
        else {
          for (let i = 0; i < this.operatorTypeOptions.length; i++) {
            if (this.operatorTypeOptions[i].value === value)
              return this.operatorTypeOptions[i].text;
          }
        }
      },
      closeModal() {
        this.isModalVisible = false;
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
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onExportButton(value) {

        this.button = value;
        if (this.button === 'hand') {
          this.onExportButton1();
        }
        if (this.button === 'statistics') {
          this.onExportButton2();
        }
        this.isModalVisible = true;
      },
      onExport() {
        if (this.button === 'hand') {
          this.onExportButton1();
        }
        if (this.button === 'statistics') {
          this.onExportButton2();
        }
      },
      onExportButton1() {
        let checkedAll, checkedIds;
        if (this.pageStatus1 === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        this.params = {
          'isAll': checkedIds.length > 0 || this.pageStatus1 === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus1 === 'charts' ? checkedIds : checkedIds.join()
        };
        this.link = `task/statistics/handexamination/generate`;
        this.name = 'Statistics-Hand';
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onPrintButton() {
        let checkedAll, checkedIds;
        if (this.pageStatus1 === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.pageStatus1 === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus1 === 'charts' ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/handexamination/generate`;
        if (this.pageStatus1 !== 'charts' && checkedIds.length === 0) {

        } else {
          printFileFromServer(link, params);
        }
      },

      onExportButton2() {
        let checkedAll, checkedIds;
        if (this.pageStatus2 === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable2.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable2.selectedTo;
        }

        this.params = {
          'isAll': checkedIds.length > 0 || this.pageStatus2 === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus2 === 'charts' ? checkedIds : checkedIds.join()
        };
        this.link = `task/statistics/suspiciongoods/generate`;
        this.name = 'Statistics-HandGoods';
        // if (this.pageStatus2 !== 'charts' && checkedIds.length === 0) {
        //
        // } else {
        //   downLoadFileFromServer(link, params, 'Statistics-Handgoods', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },

      onPrintButton2() {
        let checkedAll, checkedIds;
        if (this.pageStatus2 === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable2.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable2.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.pageStatus2 === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus2 === 'charts' ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/suspiciongoods/generate`;
        if (this.pageStatus2 !== 'charts' && checkedIds.length === 0) {

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
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-handexamination-statistics`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          //this.preViewData = [];
          this.preViewData = response.data.data;
          if (this.preViewData.totalStatistics != null) {
            this.pieChart2Options.series[0].data[0].value = this.preViewData.totalStatistics.seizure;
            this.pieChart2Options.series[0].data[1].value = this.preViewData.totalStatistics.noSeizure;
          }

          this.xDay = Object.keys(this.preViewData.detailedStatistics);

          this.barChart2Options.xAxis.data = this.xDay;
          for (let i = 0; i < this.xDay.length; i++) {
            let key = this.xDay[i];

            if (this.preViewData.detailedStatistics[key] != null) {
              this.barChart2Options.series[0].data[i] = this.preViewData.detailedStatistics[key].noSeizure;
              this.barChart2Options.series[1].data[i] = this.preViewData.detailedStatistics[key].seizure;
            }
          }
          //}
        });
      },
      getGraphData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-suspicionhandgoods-statistics`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.graphData = response.data.data;
          this.wordCloudData = [];

          let tmpKey = Object.keys(this.graphData.totalStatistics);
          let keyData = [];
          console.log(tmpKey.length);

          // this.xDay = Object.keys(this.graphData.totalStatistics);
          let xAxis = [], tmp = 0, k = 0;
          if(tmpKey.length !== 0) {
            for (let i = 0; i < tmpKey.length; i++) {
              tmp = 0;
              for (let j = 0; j < tmpKey.length; j++) {
                if (this.graphData.totalStatistics[tmpKey[tmp]] < this.graphData.totalStatistics[tmpKey[j]]) {
                  tmp = j;
                }
              }
              xAxis[k] = tmpKey[tmp];
              k++;
              i--;
              tmpKey.splice(tmp, 1);
            }
          }

          // "1000001601": `安眠药`,
          //   "1000001602": `仿真枪`,
          //   "1000001603": `玩具枪`,
          //   "1000001604": `气枪`,
          //   "1000001605": `打火机`,

          if(xAxis.length !== 0) {
            for (let i = 0; i < xAxis.length; i++) {

              if (this.graphData.totalStatistics != null) {
                let key = xAxis[i];

                keyData[i] = this.getDataCodeValue(xAxis[i]);
                this.bar3ChartOptions.series[0].data[i] = this.graphData.totalStatistics[key];

              }
            }
          }
          else {
            this.bar3ChartOptions.series[0].data = [0, 0, 0, 0, 0];
            keyData = [`安眠药`, `仿真枪`, `玩具枪`, `气枪`, `打火机`]
          }

          this.bar3ChartOptions.xAxis.data = keyData;
          console.log(this.bar3ChartOptions.xAxis.data);

          if(xAxis.length !== 0) {

            for (let i = 0; i < xAxis.length; i++) {
              this.wordCloudData.push({
                name: this.getDataCodeValue(xAxis[i]),
                weight: this.graphData.totalStatistics[xAxis[i]]
              });
            }
            this.drawWordCloud();
          }
          else {
            for(let i=0; i<keyData.length; i++){
              this.wordCloudData.push({
                name: keyData[i],
                weight: 0
              });
            }
            this.drawWordCloud();
          }

        });

      },
      onSearchButton() {

        this.getPreviewData();
        this.getGraphData();
        this.setPeriodLabel(this.filter.statWidth);
        this.$refs.taskVuetable.refresh();
        this.$refs.taskVuetable2.refresh();
      },
      onResetButton() {
        this.filter = {
          fieldId: null,
          deviceId: null,
          userCategory: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        };
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      onTaskVuetable2PaginationData(paginationData) {
        this.$refs.taskVuetable2Pagination.setPaginationData(paginationData);
        this.changeCheckAllStatus2();
      },
      onTaskVuetable2ChangePage(page) {
        this.$refs.taskVuetable2.changePage(page);
        this.changeCheckAllStatus2();
      },
      onDisplaceButton1() {
        if (this.pageStatus1 === 'charts') {
          this.pageStatus1 = 'table';
        } else {
          this.pageStatus1 = 'charts';
        }
      },
      onDisplaceButton2() {
        if (this.pageStatus2 === 'charts') {
          this.pageStatus2 = 'table';
        } else {
          this.pageStatus2 = 'charts';
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
          this.renderedCheckList.push(data.detailedStatistics[j].time);
          transformed.data.push(temp)
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
      taskVuetable2HttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckList = [];

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.taskVuetable2Items.perPage,
          filter: this.filter,
        });
      },
    },
  }
</script>

<style lang="scss">
  .hand-checks {

    display: flex;
    flex-direction: column;
    height: calc(100vh - 190px);

    .no-padding {
      .card-body {
        padding: 0;
      }
    }

    .statistics-item {
      display: flex;
      align-items: center;
      $padding-x: 50px;
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


    .bottom-part {
      display: flex;
      flex-grow: 1;

      .charts-part-2 {

        display: flex;

        & > .row {

          flex-grow: 1;

          & > .col:nth-child(1) {
            .card-body {
              display: flex;
              flex-direction: column;

              .double-pie-chart {
                display: flex;
                align-items: center;
                justify-content: flex-start;
              }

              .legend-item {
                text-align: center;

                .legend-name {
                  flex-grow: 1;

                  .legend-icon {
                    $icon-size: 12px;

                    display: inline-block;
                    width: $icon-size;
                    height: $icon-size;
                    border-radius: $icon-size/2;
                    border: solid 2px transparent;
                    margin-right: $icon-size/2;
                  }
                }

                .value {
                  font-size: 2rem;
                  font-weight: bold;
                }

                &:nth-child(1) .legend-icon {
                  border-color: #1989fa;
                }

                &:nth-child(2) .legend-icon {
                  border-color: #ffd835;
                }

                &:nth-child(3) .legend-icon {
                  border-color: #ff0000;
                }
              }
            }

          }

          & > .col:nth-child(2) {
            .card-body {
              display: flex;
              flex-direction: column;

              .bar-3-chart {

                display: flex;

                height: 100%;

                .echarts {
                  width: 100%;
                  height: 100%;
                }
              }

            }

          }

          .card {
            width: 100%;
            height: 100%;
          }
        }
      }
    }

    .charts-part-1 {

      display: flex;

      flex-direction: column;

      & > .row {

        flex-grow: 1;

        & > *:nth-child(1) {

          .card-body {
            display: flex;
            flex-direction: column;

            .pie-chart {
              width: 250px;
              height: 250px;
              display: flex;
              align-items: center;
              justify-content: flex-start;

              .echarts {
                width: 100%;
                height: 100%;
              }
            }

            .legend-group {
              width: 160px;
              display: flex;
              flex-direction: column;

              .legend-item {
                display: flex;
                align-items: center;
                margin-bottom: 16px;

                .legend-icon {
                  $size: 12px;
                  width: $size;
                  height: $size;
                  border-radius: 50%;
                  position: relative;
                  margin-right: $size;

                  &:after {
                    content: ' ';
                    display: block;
                    width: $size / 2;
                    height: $size / 2;
                    border-radius: 50%;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    background: #fff;
                  }
                }

                .legend-name {
                  flex-grow: 1;
                }

                .value {

                }


              }

              &.part-2 {
                .legend-item {

                  &:nth-child(1) .legend-icon {
                    background-color: #ff0000;
                  }

                  &:nth-child(2) .legend-icon {
                    background-color: #009900;
                  }

                }
              }

            }
          }

        }

        & > *:nth-child(2) {


          .card-body {
            display: flex;
            flex-direction: column;

            .bar-chart-1-and-2 {

              display: flex;

              height: 100%;

              .echarts {
                width: 100%;
                height: 100%;
              }
            }

          }

        }

        .card {
          width: 100%;
          height: 100%;
        }
      }

      .bar-chart-3 {
        height: 300px;

        .echarts {
          width: 100%;
          height: 100%;
        }
      }
    }


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

    .parameter-items {

      & > .col:nth-child(1) {
        flex: 1 0 0;
        display: flex;
      }

      & > .col:nth-child(2) {
        flex: 4 0 0;
      }

      .statistics-item {
        display: flex;
        align-items: center;
        $padding-x: 40px;
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

        &.type-3 {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: center;

          & > div:nth-child(1) {
            margin: 0;

            img {
              width: 100%;
              height: 100%;
            }
          }

          & > div:nth-child(2) {
            margin: 8px 0;

            span {
              font-size: 2.2rem;
              color: #fff;
            }
          }

          & > div:nth-child(3) {
            span {
              font-size: 1rem;
              color: #fff;
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
    }

  }
</style>
