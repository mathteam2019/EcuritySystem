<template>
  <div class="statistics-scan-devices">
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
            <b-form-group :label="$t('statistics.view.task-type')">
              <b-form-select v-model="filter.workMode" :options="categoryFilterData" plain/>
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
                <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.totalScan}}</span>
                <span v-else>0</span>
            </div>
            <div><span>{{this.$t('statistics.view.scan-total')}}</span></div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-row class="mb-4">
          <b-col>
            <b-card class="no-padding">
              <div class="statistics-item type-2">
                <div style="background-color: #1989fa">
                  <b-img src="/assets/img/check.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.validScan}}</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.valid-scan')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding">
              <div class="statistics-item type-2">
                <div style="background-color: #1989fa">
                  <b-img src="/assets/img/check.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.validScanRate)}}%</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.valid-scan-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #cccccc;">
                  <b-img src="/assets/img/forbidden.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.invalidScan}}</span>
                <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.invalid-scan')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #cccccc;">
                  <b-img src="/assets/img/forbidden.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.invalidScanRate)}}%</span>
                    <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.invalid-scan-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #009900;">
                  <b-img src="/assets/img/round_check.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.passedScan}}</span>
                <span v-else>0</span>
                  </div>
                  <div><span>{{$t('permission-management.permission-control.pending-success')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #009900;">
                  <b-img src="/assets/img/round_check.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.passedScanRate)}}%</span>
                <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.passed-scan-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #ff6600;">
                  <b-img src="/assets/img/bell_icon.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.alarmScan}}</span>
                <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.alarm-scan')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="no-padding" style="background-color: #fff;">
              <div class="statistics-item type-2">
                <div style="background-color: #ff6600;">
                  <b-img src="/assets/img/bell_icon.svg"/>
                </div>
                <div>
                  <div>
                    <span v-if="preViewData.totalStatistics!=null">{{Math.round(preViewData.totalStatistics.alarmScanRate)}}%</span>
                <span v-else>0</span>
                  </div>
                  <div><span>{{$t('statistics.view.alarm-scan-rate')}}</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
        </b-row>
      </b-col>

    </b-row>

    <b-row class="mt-4 mb-3">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton()">
            <i class="icofont-exchange"/>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('scan_statistics_export')" @click="onExportButton()">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('scan_statistics_print')" @click="onPrintButton()">
            <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>

    <b-row class="bottom-part mb-3">
      <b-col v-if="pageStatus==='charts'" class="charts-part">
        <b-row>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>{{$t('maintenance-management.process-task.scan')}}</h5>
              </b-card-header>
              <div class="w-100 flex-grow-1 d-flex flex-column justify-content-around">

                <div class="d-flex align-items-center justify-content-around">
                  <div>

                    <v-chart :options="doublePieChartOptions" :autoresize="true" style="width: 300px; height: 300px;"/>

                  </div>
                  <div class="legend-group">
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.invalid-scan')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.invalidScan}}</div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.valid-scan')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.validScan}}</div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.alarm-scan')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.alarmScan}}</div>

                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('permission-management.permission-control.pending-success')}}</div>
                      <div class="value" v-if="preViewData.totalStatistics!=null">{{preViewData.totalStatistics.passedScan}}</div>
                    </div>
                  </div>
                </div>
              </div>

            </b-card>
          </b-col>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>{{$t('statistics.view.scan-history')}}</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div>

                  <v-chart :options="bar3ChartOptions" :autoresize="true" style="width: 100%; height: 300px;"/>

                </div>
              </div>

            </b-card>
          </b-col>
        </b-row>
      </b-col>
      <b-col v-if="pageStatus==='table'" class="table-part">
        <b-card class="flex-grow-1 ">
          <b-card-header>

            <h5 class="text-center my-4">{{$t('statistics.view.table-title')}}</h5>

          </b-card-header>

          <div class="flex-grow-1 ">
            <div class="container-fluid">
              <b-row class="no-gutters mb-2">
                <b-col cols="1"><b>{{$t('knowledge-base.site')}}:</b></b-col>
                <b-col cols="11">
                  <span v-if="filter.fieldId === null">{{allField}}</span>
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
                <b-col cols="11"><span>{{$t('statistics.view.operator')}}</span></b-col>
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
                <b-col cols="11">
                  <span>{{getDateTimeFormat(filter.startTime)}}-{{getDateTimeFormat(filter.endTime)}}</span>
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
              <b-row class="no-gutters">

                <b-col cols>

                  <div class="table-wrapper table-responsive">
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

                </b-col>

              </b-row>
            </div>
          </div>
        </b-card>

      </b-col>
    </b-row>
    <b-modal  centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export') }}</h3>
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

  import {checkPermissionItem, getDirection,getLocale} from "../../../utils";
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
      this.setPeriodLabel('hour');
    },
    data() {

      let doublePieChartData = {
        '无效扫描': {
          value: 0,
          color: '#cccccc'
        },
        '有效扫描': {
          value: 0,
          color: '#1989fa',
        },
        '报警': {
          value: 0,
          color: '#ff6600',
        },
        '通过': {
          value: 0,
          color: '#009900'
        },
      };

      return {
        selectedValueRange: {
          start: new Date(2018, 12, 9),
          end: new Date(2018, 12, 18)
        },
        formats: {
          input: ['YYYYMMDD']
        },
        doublePieChartOptions: {
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
            doublePieChartData['无效扫描'].color,
            doublePieChartData['有效扫描'].color,
            doublePieChartData['报警'].color,
            doublePieChartData['通过'].color,
          ],
          series: [
            {
              type: 'pie',
              hoverAnimation: false,
              radius: ['80%', '90%'],
              avoidLabelOverlap: false,
              label: {
                normal: {
                  show: true,
                  position: 'outside',
                },
              },
              labelLine: {
                show: false,
                length: -34,
                length2: -30
              },
              data: [
                {value: doublePieChartData['无效扫描'].value, name: this.$t('statistics.view.invalid-scan')},
                {value: doublePieChartData['有效扫描'].value, name: this.$t('statistics.view.valid-scan')},
              ]
            },
            {
              type: 'pie',
              hoverAnimation: false,
              radius: ['40%', '50%'],
              avoidLabelOverlap: false,
              label: {
                normal: {
                  show: true,
                  position: 'outside',
                  align: 'center'
                }
              },
              labelLine: {
                show: false,
                length: -40,
                length2: -15
              },
              data: [
                {value: doublePieChartData['报警'].value, name: this.$t('statistics.view.alarm-scan')},
                {value: doublePieChartData['通过'].value, name: this.$t('permission-management.permission-control.pending-success')},
              ]
            }
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
            data: [this.$t('permission-management.permission-control.pending-success'), this.$t('statistics.view.alarm-scan'), this.$t('statistics.view.invalid-scan')],
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
          color: ['#ff6600', '#009900', '#cccccc'],
          series: [
            {
              name: this.$t('permission-management.permission-control.pending-success'),
              type: 'bar',
              stack: this.$t('statistics.view.total'),
              data: [0]
            },
            {
              name: this.$t('statistics.view.alarm-scan'),
              type: 'bar',
              stack: this.$t('statistics.view.total'),

              data: [0]
            },
            {
              name: this.$t('statistics.view.invalid-scan'),
              type: 'bar',
              stack: this.$t('statistics.view.total'),
              data: []
            }
          ]
        },

        isExpanded: false,
        isCheckAll: false,
        pageStatus: 'charts',
	link: '',
        params: {},
        name: '',

        fileSelection: [],
        renderedCheckList:[],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
	isModalVisible: false,

        filter: {
          fieldId: null,
          workMode : null,
          deviceId: null,
          userCategory: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },

        siteData: [],
        allField: '',
        allDevice:'',
        preViewData: [],
        manualDeviceOptions: [],

        xYear: [],
        xQuarter: ['1', '2', '3', '4'],
        xMonth: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        monthLabel: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
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
        categoryFilterData: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: "1000002602", text: this.$t('maintenance-management.process-task.scan')},
          {value: "1000002603", text: this.$t('maintenance-management.process-task.judge')},
          {value: "1000002604", text: this.$t('maintenance-management.process-task.hand')}
        ],
        securityDeviceOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'security_device_1', text: "安检仪001"},
          {value: 'security_device_2', text: "安检仪002"},
          {value: 'security_device_3', text: "安检仪003"},
        ],
        operatorTypeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000002404', text: this.$t('maintenance-management.process-task.scan')},
          {value: '1000002403', text: this.$t('maintenance-management.process-task.judge')},
          {value: '1000002402', text: this.$t('maintenance-management.process-task.hand')},
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
          apiUrl: `${apiBaseUrl}/task/statistics/scan`,
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
              title : this.setPeriodLabel,
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'totalScan',
              title: this.$t('statistics.view.scan-total'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'validScan',
              title: this.$t('statistics.view.valid-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'validScanRate',
              title: this.$t('statistics.view.valid-scan-rate-table'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (validScanRate) => {
                if (validScanRate == null) return '';
                return validScanRate.toFixed(1);
              }
            },
            {
              name: 'invalidScan',
              title: this.$t('statistics.view.invalid-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'invalidScanRate',
              title: this.$t('statistics.view.invalid-scan-rate-table'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (invalidScanRate) => {
                if (invalidScanRate == null) return '';
                return invalidScanRate.toFixed(1);
              }

            },
            {
              name: 'passedScan',
              title: this.$t('statistics.view.passed-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'passedScanRate',
              title: this.$t('statistics.view.passed-scan-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (passedScanRate) => {
                if (passedScanRate == null) return '';
                return passedScanRate.toFixed(1);
              }
            },
            {
              name: 'alarmScan',
              title: this.$t('statistics.view.alarm-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'alarmScanRate',
              title: this.$t('statistics.view.alarm-scan-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (alarmScanRate) => {
                if (alarmScanRate == null) return '';
                return alarmScanRate.toFixed(1);
              }
            },

          ],
          perPage: 10,
        },

      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
        this.changeCheckAllStatus();
      },

      siteData: function (newVal, oldVal) {

          this.onSiteOption = [];
          let nest = (newVal, id = 0, depth = 1) =>
              newVal
                  .filter(item => item.parentFieldId == id)
                  .map(item => ({
                      data: {fieldId: item.fieldId},
                      children: nest(newVal, item.fieldId, depth + 1),
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

          let changeFieldTree = (treeData, index) => {
              if (!treeData || treeData.length === 0) {
                  return;
              }
              let tmp = treeData;
              for (let i = 0; i < tmp.length; i++) {
                  changeFieldTree(tmp[i].children, index + 1);
                  this.onSiteOption.unshift({
                      value: tmp[i].data.fieldId,
                      html: `${generateSpace(index)}${tmp[i].text}`
                  });
              }
          };

          changeFieldTree(treeData, 1);
          this.onSiteOption.unshift({
              value: null,
              html: `${this.$t('permission-management.all')}`
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
      selectAll(value){
        this.$refs.taskVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.taskVuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        this.$refs.taskVuetable.isCheckAllStatus=false;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.taskVuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNone();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAll(true);
            }
          }
        }
        else {
          this.selectNone();
        }

      },
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
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
      getDeviceLabel(value){
        if(value===null||this.manualDeviceOptions===null) return "";
        else{
          for(let i=0; i<this.manualDeviceOptions.length; i++){
            if(this.manualDeviceOptions[i].value===value)
              return this.manualDeviceOptions[i].text;
          }
        }
      },
      getCategoryLabel(value){
        if(value===null||this.operatorTypeOptions===null) return "";
        else{
          for(let i=0; i<this.operatorTypeOptions.length; i++){
            if(this.operatorTypeOptions[i].value===value)
              return this.operatorTypeOptions[i].text;
          }
        }
      },
      closeModal() {
        this.isModalVisible = false;
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
        if(dataTime==null)return '';
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
       let checkedAll, checkedIds;
        if (this.pageStatus === 'charts') {
          checkedAll = true;
          checkedIds = "";
        }
        else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0  || this.pageStatus==='charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus ==='charts'?checkedIds:checkedIds.join()
        };
        this.link = `task/statistics/scan/generate`;
        this.name = this.$t('menu.statistics-scan-devices');
        this.isModalVisible = true;
      },
      onExport(){
        let checkedAll, checkedIds;
        if (this.pageStatus === 'charts') {
          checkedAll = true;
          checkedIds = "";
        }
        else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 || this.pageStatus==='charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus ==='charts'?checkedIds:checkedIds.join()
        };
        let link = `task/statistics/scan/generate`;
        if(this.pageStatus!=='charts'&& checkedIds.length === 0){

        }else {
        downLoadFileFromServer(link, params, 'Statistics-Scan', this.fileSelection);
          this.hideModal('model-export')
        }
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onPrintButton() {
        let checkedAll, checkedIds;
        if (this.pageStatus === 'charts') {
          checkedAll = true;
          checkedIds = "";
        }
        else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 || this.pageStatus==='charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus ==='charts'?checkedIds:checkedIds.join()
        };
        let link = `task/statistics/scan/generate`;
        if(this.pageStatus!=='charts'&& checkedIds.length === 0){

        }else {
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
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {

            allFieldStr = allFieldStr + ", " + this.siteData[i].fieldDesignation;

          }
          this.allField = allFieldStr;
        })
          .catch((error) => {
          });

      },

      getPreviewData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/scan`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;
          if(this.preViewData.totalStatistics!=null) {

          this.doublePieChartOptions.series[0].data[0].value = this.preViewData.totalStatistics.invalidScan;
          this.doublePieChartOptions.series[0].data[1].value = this.preViewData.totalStatistics.validScan;
          this.doublePieChartOptions.series[1].data[0].value = this.preViewData.totalStatistics.alarmScan;
          this.doublePieChartOptions.series[1].data[1].value = this.preViewData.totalStatistics.passedScan;
}
          else {
            this.doublePieChartOptions.series[0].data[0].value = 0;
            this.doublePieChartOptions.series[0].data[1].value = 0;
            this.doublePieChartOptions.series[1].data[0].value = 0;
            this.doublePieChartOptions.series[1].data[1].value = 0;
          }

          // if (this.filter.statWidth === 'year') {
          //   this.bar3ChartOptions.xAxis.data = this.xHour;
          // } else {
            this.xDay = Object.keys(this.preViewData.detailedStatistics);

            this.bar3ChartOptions.xAxis.data = this.xDay;
            for (let i = 0; i < this.xDay.length; i++) {
               let key = this.xDay[i];

              if (this.preViewData.detailedStatistics[key] != null) {
                this.bar3ChartOptions.series[0].data[i] = this.preViewData.detailedStatistics[key].passedScan;
                this.bar3ChartOptions.series[1].data[i] = this.preViewData.detailedStatistics[key].alarmScan;
                this.bar3ChartOptions.series[2].data[i] = this.preViewData.detailedStatistics[key].invalidScan;
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
          workMode: null,
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
      onDisplaceButton() {
        if (this.pageStatus === 'charts') {
          this.pageStatus = 'table';
        } else {
          this.pageStatus = 'charts';
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
          filter: this.filter
        });
      },

    },

  }
</script>

<style lang="scss">
  .col-form-label {
    margin-bottom: 1px;
  }
  .statistics-scan-devices {

    display: flex;
    flex-direction: column;

    .no-padding {
      .card-body {
        padding: 0;
      }
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
              font-weight: bold;
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


    .bottom-part {
      display: flex;
      flex-grow: 1;

      .charts-part {

        display: flex;

        & > .row {

          flex-grow: 1;

          & > *:nth-child(1) {
            flex: 2 1 0;

            .card-body {
              display: flex;
              flex-direction: column;


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

                  &:nth-child(1) .legend-icon {
                    background-color: #cccccc;
                  }

                  &:nth-child(2) .legend-icon {
                    background-color: #1989fa;
                  }

                  &:nth-child(3) .legend-icon {
                    background-color: #ff6600;
                  }

                  &:nth-child(4) .legend-icon {
                    background-color: #009900;
                  }
                }

              }
            }

          }

          & > *:nth-child(2) {
            flex: 3 1 0;

            .card-body {
              display: flex;
              flex-direction: column;

            }

          }

          .card {
            width: 100%;
            height: 100%;
          }
        }
      }

      .table-part {

        display: flex;

        .card-body {
          display: flex;
          flex-direction: column;
          padding: 20px;
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

  }
</style>

