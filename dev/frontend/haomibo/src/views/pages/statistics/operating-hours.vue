<template>
  <div class="operating-hours">
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
            <b-form-group :label="$t('device-management.device-table.device-type')">
              <b-form-select v-model="filter.deviceType" :options="categoryFilterDatas" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('maintenance-management.maintenance-task.device')">
              <b-form-input v-model="filter.deviceName"/>
            </b-form-group>
          </b-col>

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

          <b-col>
            <b-form-group :label="$t('statistics.view.step-size')">
              <b-form-select v-model="filter.statWidth" :options="statisticalStepSizeOptions" plain/>
            </b-form-group>
          </b-col>
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

    <b-row>
      <b-col>
        <b-card class="no-padding" style="background-color: #1989fa;">
          <div class="statistics-item type-1">
            <div>
              <b-img src="/assets/img/clock.svg"/>
            </div>
            <div>
              <div><span class="span-font">D{{totalData['day'].value}} {{totalData['hour'].value}}: {{totalData['minute'].value}}: {{totalData['second'].value}}</span>
              </div>
              <div><span>{{$t('statistics.operating-hours.total-time') }}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #1989fa;">
              <b-img src="/assets/img/scan.svg"/>
            </div>
            <div>
              <div><span class="span-font">D{{scanData['day'].value}} {{scanData['hour'].value}}: {{scanData['minute'].value}}: {{scanData['second'].value}}</span>
              </div>
              <div><span>{{$t('statistics.operating-hours.security-time') }}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: red;">
              <b-img src="/assets/img/round_check.svg"/>
            </div>
            <div>
              <div><span class="span-font">D{{judgeData['day'].value}} {{judgeData['hour'].value}}: {{judgeData['minute'].value}}: {{judgeData['second'].value}}</span>
              </div>
              <div><span>{{$t('statistics.operating-hours.judge-time') }}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #ffd835;">
              <b-img src="/assets/img/hand_check_icon.svg"/>
            </div>
            <div>
              <div><span class="span-font">D{{handData['day'].value}} {{handData['hour'].value}}: {{handData['minute'].value}}: {{handData['second'].value}}</span>
              </div>
              <div><span>{{$t('statistics.operating-hours.hand-time') }}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
    </b-row>
    <b-row class="mt-4 mb-2">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton()">
            <i class="icofont-exchange"/>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('device_statistics_export')" @click="onExportButton()">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('device_statistics_print')" @click="onPrintButton()">
            <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>

    <b-row class="bottom-part mt-3 mb-3">
      <b-col v-if="pageStatus==='charts'" class="charts-part">
        <b-row>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>{{$t('statistics.operating-hours.time-statistics') }}</h5>
              </b-card-header>
              <div class="w-100 flex-grow-1 d-flex flex-column justify-content-around">
                <div class="d-flex align-items-center justify-content-around">
                  <div class="double-pie-chart">
                    <v-chart :options="doublePieChartOptions" :autoresize="true"/>
                  </div>
                </div>
                <b-row>
                  <b-col class="legend-item">
                    <div class="value">{{scanData['rate'].value}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      {{$t('statistics.operating-hours.security-time') }}
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{handData['rate'].value}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      {{$t('statistics.operating-hours.judge-time') }}
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{judgeData['rate'].value}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      {{$t('statistics.operating-hours.hand-time') }}
                    </div>
                  </b-col>
                </b-row>
              </div>

            </b-card>
          </b-col>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>{{$t('statistics.operating-hours.time-statistics') }}</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div class="bar-3-chart">
                  <v-chart :options="bar3ChartOptions" :autoresize="true"/>
                </div>
              </div>

            </b-card>
          </b-col>
        </b-row>
      </b-col>
      <b-col v-if="pageStatus==='table'">
        <b-card>
          <b-card-header>
            <h5 class="text-center my-4">{{$t('statistics.operating-hours.title-table') }}</h5>
          </b-card-header>

          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('knowledge-base.site')}}:</b></b-col>
            <b-col cols="11">
              <span>{{allField}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.evaluate-monitors.security-device')}}:</b></b-col>
            <b-col cols="11">
              <span>{{allDevice}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.view.operator-type')}}:</b></b-col>
            <b-col cols="11"><span>引导员, 判图员, 手检员</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.view.operator')}}:</b></b-col>
            <b-col cols="11">
              <span v-if="filter.userName===null">张三, 李四, 王五</span>
              <span v-else>{{filter.userName}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>{{$t('statistics.evaluate-monitors.time')}}:</b></b-col>
            <b-col cols="11">
              <span>{{this.getDateTimeFormat(filter.startTime)}}-{{this.getDateTimeFormat(filter.endTime)}}</span>
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
  import Vuetable from '../../../components/Vuetable2/Vuetable';
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import 'vue-tree-halower/dist/halower-tree.min.css';
  import Switches from 'vue-switches';
  import ECharts from 'vue-echarts';
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

  import {checkPermissionItem, getDirection} from "../../../utils";
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

      this.getCategoryData();
      this.getSiteOption();
      this.getPreviewData();
      this.getGraphData();
    },
    data() {


      let doublePieChartData = {
        '安检仪累计运行时长': {
          value: 0,
          color: '#1989fa'
        },
        '判图站累计运行时长': {
          value: 0,
          color: '#ff0000',
        },
        '手检站累计运行时长': {
          value: 0,
          color: '#ffd835',
        }
      };

      return {
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
            doublePieChartData['安检仪累计运行时长'].color,
            doublePieChartData['判图站累计运行时长'].color,
            doublePieChartData['手检站累计运行时长'].color
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
                {value: doublePieChartData['安检仪累计运行时长'].value, name: this.$t('statistics.operating-hours.security-time') },
                {value: doublePieChartData['判图站累计运行时长'].value, name: this.$t('statistics.operating-hours.judge-time') },
                {value: doublePieChartData['手检站累计运行时长'].value, name: this.$t('statistics.operating-hours.hand-time') },
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
            data: [this.$t('statistics.operating-hours.security') , this.$t('statistics.operating-hours.judge') , this.$t('statistics.operating-hours.hand') ],
            icon: 'rect',
            right: 25,
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
            axisLabel: {
              interval: 0
            },
            axisTick: {
              show: false,
              interval: 0,
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
          color: ['#1989fa', '#ff0000', '#ffd835'],
          series: [
            {
              name: this.$t('statistics.operating-hours.security') ,
              type: 'bar',
              stack: this.$t('statistics.view.total'),
              data: [0]
            },
            {
              name: this.$t('statistics.operating-hours.judge') ,
              type: 'bar',
              stack: this.$t('statistics.view.total'),

              data: [0]
            },
            {
              name: this.$t('statistics.operating-hours.hand') ,
              type: 'bar',
              stack: this.$t('statistics.view.total'),
              data: []
            }
          ]
        },

        pageStatus: 'charts',
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

        filter: {
          deviceName: null,
          deviceType: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },

        siteData: [],
        allField: '',
        allDevice: '',
        preViewData: [],
        graphData: [],
        total: [],
        scan: [],
        judge: [],
        hand: [],

        categoryData: [],
        categoryFilterData: [],
        categoryFilterDatas: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 1000001901, text: this.$t('statistics.operating-hours.security') },
          {value: 1000001902, text: this.$t('statistics.operating-hours.judge') },
          {value: 1000001903, text: this.$t('statistics.operating-hours.hand') }
        ],

        statisticalStepSizeOptions: [
          {value: 'hour', text: this.$t('statistics.hour')},
          {value: 'day', text: this.$t('statistics.day')},
          {value: 'week', text: this.$t('statistics.week')},
          {value: 'month', text: this.$t('statistics.month')},
          {value: 'quarter', text: this.$t('statistics.quarter')},
          {value: 'year', text: this.$t('statistics.year')},
        ],

        totalData: {
          'day': {
            value: 0
          },
          'hour': {
            value: 0
          },
          'minute': {
            value: 0
          },
          'second': {
            value: 0
          },
        },

        scanData: {
          'day': {
            value: 0
          },
          'hour': {
            value: 0
          },
          'minute': {
            value: 0
          },
          'second': {
            value: 0
          },
          'rate': {
            value: 0
          }
        },

        judgeData: {
          'day': {
            value: 0
          },
          'hour': {
            value: 0
          },
          'minute': {
            value: 0
          },
          'second': {
            value: 0
          },
          'rate': {
            value: 0
          }
        },

        handData: {
          'day': {
            value: 0
          },
          'hour': {
            value: 0
          },
          'minute': {
            value: 0
          },
          'second': {
            value: 0
          },
          'rate': {
            value: 0
          }
        },

        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/statistics/get-statistics-filter-by-device`,
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
              name: 'name',
              title:  this.$t('statistics.operating-hours.device-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'scanStatistics',
              title: this.$t('statistics.view.scan-total'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.totalScan;
              }
            },
            {
              name: 'scanStatistics',
              title: this.$t('statistics.view.invalid-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.invalidScan;
              }
            },
            {
              name: 'scanStatistics',
              title: this.$t('statistics.view.invalid-scan-rate-table'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.invalidScanRate.toFixed(1);
              }
            },
            {
              name: 'judgeStatistics',
              title: this.$t('statistics.view.judge-total'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.totalJudge;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: this.$t('statistics.view.hand-total'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.totalHandExamination;
              }
            },
            {
              name: 'judgeStatistics',
              title: this.$t('statistics.view.no-suspiction-judge'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.noSuspictionJudge;
              }
            },
            {
              name: 'judgeStatistics',
              title: this.$t('statistics.view.no-suspiction-judge-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.noSuspictionJudgeRate.toFixed(1);
              }
            },
            {
              name: 'handExaminationStatistics',
              title: this.$t('statistics.view.no-seizure'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.noSeizureHandExamination;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: this.$t('statistics.view.no-seizure-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.noSeizureHandExaminationRate.toFixed(1);
              }
            },
            {
              name: 'handExaminationStatistics',
              title: this.$t('statistics.view.seizure'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.seizureHandExamination;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: this.$t('statistics.view.seizure-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.seizureHandExaminationRate.toFixed(1);
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
      'operatingLogTableItems.perPage': function (newVal) {
        this.$refs.operatingLogTable.refresh();
      },
      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.categorySelectOptions = [];
        if (newVal.length === 0) {
          this.categorySelectOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        } else {
          this.categorySelectOptions = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.categoryFilterData = JSON.parse(JSON.stringify(this.categorySelectOptions));
        this.categoryFilterData.push({value: null, text: `${this.$t('permission-management.all')}`})
      },

    },
    methods: {
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
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

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

      getDateTimeFormat(dataTime) {
        if (dataTime == null) return '';
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll, checkedIds;
        if (this.pageStatus === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        this.params = {
          'isAll': checkedIds.length > 0 || this.pageStatus === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus === 'charts' ? checkedIds : checkedIds.join()
        };
        this.link = `task/statistics/devicestatistics/generate`;
        this.name = 'Statistics-OperatingHour';
        this.isModalVisible = true;
      },
      onExport() {
        let checkedAll, checkedIds;
        if (this.pageStatus === 'charts') {
          checkedAll = true;
          checkedIds = "";
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.pageStatus === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus === 'charts' ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/devicestatistics/generate`;
        if (this.pageStatus !== 'charts' && checkedIds.length === 0) {

        } else {
          downLoadFileFromServer(link, params, 'Statistics-OperatingHour', this.fileSelection);
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
        } else {
          checkedAll = this.$refs.taskVuetable.checkedAllStatus;
          checkedIds = this.$refs.taskVuetable.selectedTo;
        }

        let params = {
          'isAll': checkedIds.length > 0 || this.pageStatus === 'charts' ? checkedAll : true,
          'filter': {'filter': this.filter},
          'idList': this.pageStatus === 'charts' ? checkedIds : checkedIds.join()
        };
        let link = `task/statistics/devicestatistics/generate`;
        if (this.pageStatus !== 'charts' && checkedIds.length === 0) {

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
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {

            allFieldStr = allFieldStr + ", " + this.siteData[i].fieldDesignation;

          }
          this.allField = allFieldStr;
        })
          .catch((error) => {
          });

      },

      getGraphData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-statistics-filter-by-device`, {
          filter: this.filter
        }).then((response) => {
          this.graphData = response.data.data;
          this.bar3ChartOptions.xAxis.data = [];
          let keyData = Object.keys(this.graphData.detailedStatistics);
          let xAxisChart = [];
          let allUserStr = "";

          for (let i = 0; i < keyData.length; i++) {

            let key = keyData[i];
            if (i === 0) {
              allUserStr = allUserStr + this.graphData.detailedStatistics[key].name;
            } else {
              allUserStr = allUserStr + ", " + this.graphData.detailedStatistics[key].name;
            }

            xAxisChart[i] = this.graphData.detailedStatistics[key].name;
            if (this.graphData.detailedStatistics[key].scanStatistics != null) {
              this.bar3ChartOptions.series[0].data[i] = this.graphData.detailedStatistics[key].scanStatistics.workingSeconds;
            } else {
              this.bar3ChartOptions.series[0].data[i] = 0;
            }
            if (this.graphData.detailedStatistics[key].judgeStatistics != null) {
              this.bar3ChartOptions.series[1].data[i] = this.graphData.detailedStatistics[key].judgeStatistics.workingSeconds;
            } else {
              this.bar3ChartOptions.series[1].data[i] = 0;
            }
            if (this.graphData.detailedStatistics[key].handExaminationStatistics != null) {
              this.bar3ChartOptions.series[2].data[i] = this.graphData.detailedStatistics[key].handExaminationStatistics.workingSeconds;
            } else {
              this.bar3ChartOptions.series[2].data[i] = 0;
            }
          }
          this.allDevice = allUserStr;

          this.bar3ChartOptions.xAxis.data = xAxisChart;
        })
      },
      getPreviewData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-statistics-filter-by-device`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;

          let totalSeconds = this.preViewData.totalStatistics.scanStatistics.workingSeconds + this.preViewData.totalStatistics.judgeStatistics.workingSeconds + this.preViewData.totalStatistics.handExaminationStatistics.workingSeconds;
          let scanSeconds = this.preViewData.totalStatistics.scanStatistics.workingSeconds;
          let judgeSeconds = this.preViewData.totalStatistics.judgeStatistics.workingSeconds;
          let handSeconds = this.preViewData.totalStatistics.handExaminationStatistics.workingSeconds;

          this.totalData['second'].value = totalSeconds % 60;
          this.totalData['minute'].value = ((totalSeconds - totalSeconds % 60) / 60) % 60;
          this.totalData['hour'].value = (((totalSeconds - totalSeconds % 60) / 60 - (((totalSeconds - totalSeconds % 60) / 60) % 60)) / 60) % 24;
          this.totalData['day'].value = (((totalSeconds - totalSeconds % 60) / 60 - (((totalSeconds - totalSeconds % 60) / 60) % 60)) / 60 - (((totalSeconds - totalSeconds % 60) / 60 - (((totalSeconds - totalSeconds % 60) / 60) % 60)) / 60) % 24) / 24;
          this.scanData['second'].value = scanSeconds % 60;
          this.scanData['minute'].value = ((scanSeconds - scanSeconds % 60) / 60) % 60;
          this.scanData['hour'].value = (((scanSeconds - scanSeconds % 60) / 60 - (((scanSeconds - scanSeconds % 60) / 60) % 60)) / 60) % 24;
          this.scanData['day'].value = (((scanSeconds - scanSeconds % 60) / 60 - (((scanSeconds - scanSeconds % 60) / 60) % 60)) / 60 - (((scanSeconds - scanSeconds % 60) / 60 - (((scanSeconds - scanSeconds % 60) / 60) % 60)) / 60) % 24) / 24;
          this.judgeData['second'].value = judgeSeconds % 60;
          this.judgeData['minute'].value = ((judgeSeconds - judgeSeconds % 60) / 60) % 60;
          this.judgeData['hour'].value = (((judgeSeconds - judgeSeconds % 60) / 60 - (((judgeSeconds - judgeSeconds % 60) / 60) % 60)) / 60) % 24;
          this.judgeData['day'].value = (((judgeSeconds - judgeSeconds % 60) / 60 - (((judgeSeconds - judgeSeconds % 60) / 60) % 60)) / 60 - (((judgeSeconds - judgeSeconds % 60) / 60 - (((judgeSeconds - judgeSeconds % 60) / 60) % 60)) / 60) % 24) / 24;
          this.handData['second'].value = handSeconds % 60;
          this.handData['minute'].value = ((handSeconds - handSeconds % 60) / 60) % 60;
          this.handData['hour'].value = (((handSeconds - handSeconds % 60) / 60 - (((handSeconds - handSeconds % 60) / 60) % 60)) / 60) % 24;
          this.handData['day'].value = (((handSeconds - handSeconds % 60) / 60 - (((handSeconds - handSeconds % 60) / 60) % 60)) / 60 - (((handSeconds - handSeconds % 60) / 60 - (((handSeconds - handSeconds % 60) / 60) % 60)) / 60) % 24) / 24;

          this.scanData['rate'].value = Math.round(scanSeconds / totalSeconds * 100);
          this.judgeData['rate'].value = Math.round(judgeSeconds / totalSeconds * 100);
          this.handData['rate'].value = Math.round(handSeconds / totalSeconds * 100);

          this.doublePieChartOptions.series[0].data[0].value = this.scanData['rate'].value;
          this.doublePieChartOptions.series[0].data[1].value = this.judgeData['rate'].value;
          this.doublePieChartOptions.series[0].data[2].value = this.handData['rate'].value;


        }).catch((error) => {
        });
      },

      onSearchButton() {

        this.getGraphData();
        this.getPreviewData();
        this.$refs.taskVuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          deviceName: null,
          deviceType: null,
          statWidth: 'hour',
          startTime: null,
          endTime: null
        };

        //this.getGraphData();
        //this.getPreviewData();
        //this.$refs.taskVuetable.refresh();

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
          this.renderedCheckList.push(data.detailedStatistics[j].id);
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
  .operating-hours {

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
      $padding-x: 25px;
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
            font-size: 1.5rem;
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

      .charts-part {

        display: flex;

        & > .row {

          flex-grow: 1;

          & > .col:nth-child(1) {
            flex: 2 1 0;

            .card-body {
              display: flex;
              flex-direction: column;

              .double-pie-chart {
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
            flex: 3 1 0;

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
