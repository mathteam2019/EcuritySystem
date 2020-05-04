<template>
  <div class="statistics-view">
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
            <b-form-group :label="$t('statistics.view.operator-type')">
              <b-form-select v-model="filter.userCategory" :options="operatorTypeOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.operator')">
              <b-form-input v-model="filter.userName"/>
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

          <b-col>
            <b-form-group :label="$t('statistics.view.step-size')">
              <b-form-select v-model="filter.statWidth" :options="statisticalStepSizeOptions" plain/>
            </b-form-group>
          </b-col>

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

    <b-row>
      <b-col>
        <b-card class="no-padding" style="background-color: #122881;">
          <div class="statistics-item type-1">
            <div>
              <b-img draggable="false" src="/assets/img/scan.svg"/>
            </div>
            <div>
              <div>
                <span
                  v-if="preViewData.scanStatistics!=null">{{preViewData.scanStatistics.totalScan}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('maintenance-management.process-task.scan')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #1989fa;">
              <b-img draggable="false" src="/assets/img/check.svg"/>
            </div>
            <div>
              <div>
                <span
                  v-if="preViewData.scanStatistics!=null">{{preViewData.scanStatistics.validScan}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('statistics.view.valid-scan')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #009900;">
              <b-img draggable="false" src="/assets/img/round_check.svg"/>
            </div>
            <div>
              <div>
                <span
                  v-if="preViewData.scanStatistics!=null">{{preViewData.scanStatistics.passedScan}}</span>
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
            <div style="background-color: #ff6600;">
              <b-img draggable="false" src="/assets/img/bell_icon.svg"/>
            </div>
            <div>
              <div>
                <span
                  v-if="preViewData.scanStatistics!=null">{{preViewData.scanStatistics.alarmScan}}</span>
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
            <div style="background-color: #cccccc;">
              <b-img draggable="false" src="/assets/img/forbidden.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.scanStatistics!=null">{{preViewData.scanStatistics.invalidScan}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('statistics.view.invalid-scan')}}</span></div>
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
              <b-img draggable="false" src="/assets/img/picture.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.judgeStatistics!=null">{{preViewData.judgeStatistics.totalJudge}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('maintenance-management.process-task.judge')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #009900;">
              <b-img draggable="false" src="/assets/img/round_check.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.judgeStatistics!=null">{{preViewData.judgeStatistics.noSuspictionJudge}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('knowledge-base.no-suspect')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #2bace2;">
          <div class="statistics-item type-1">
            <div>
              <b-img draggable="false" src="/assets/img/hand_check_icon.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.handExaminationStatistics!=null">{{preViewData.handExaminationStatistics.totalHandExamination}}</span>
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
            <div style="background-color: #009900;">
              <b-img draggable="false" src="/assets/img/glass_delete_icon.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.handExaminationStatistics!=null">{{preViewData.handExaminationStatistics.noSeizureHandExamination}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('knowledge-base.no-seized')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #ff0000;">
              <b-img draggable="false" src="/assets/img/glass_check_icon.svg"/>
            </div>
            <div>
              <div>
                <span v-if="preViewData.handExaminationStatistics!=null">{{preViewData.handExaminationStatistics.seizureHandExamination}}</span>
                <span v-else>0</span>
              </div>
              <div><span>{{$t('knowledge-base.seized')}}</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
    </b-row>

    <b-row class="mt-4 mb-3">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton()">
            <i class="icofont-exchange"/>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('preview_statistics_export')" @click="onExportButton()">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default"
                    :disabled="checkPermItem('preview_statistics_print')" @click="onPrintButton()">
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

                    <v-chart :options="doublePieChartOptions" :autoresize="true" style="width:300px; height: 300px;"/>

                  </div>
                  <div class="legend-group">
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.invalid-scan')}}</div>
                      <div class="value" v-if="preViewData.scanStatistics!=null">
                        {{preViewData.scanStatistics.invalidScan}}
                      </div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.valid-scan')}}</div>
                      <div class="value" v-if="preViewData.scanStatistics!=null">
                        {{preViewData.scanStatistics.validScan}}
                      </div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('statistics.view.alarm-scan')}}</div>
                      <div class="value" v-if="preViewData.scanStatistics!=null">
                        {{preViewData.scanStatistics.alarmScan}}
                      </div>

                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">{{$t('permission-management.permission-control.pending-success')}}</div>
                      <div class="value" v-if="preViewData.scanStatistics!=null">
                        {{preViewData.scanStatistics.passedScan}}
                      </div>
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
                <b-col cols="11">
                  <span v-if="filter.userCategory === null">{{$t('personal-inspection.all')}}</span>
                  <span v-else>{{getCategoryLabel(filter.userCategory)}}</span>
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

                <b-col>

                  <div class="table-wrapper table-responsive">
                    <div v-show="loadingTable" class="overlay_statistics flex flex-column items-center justify-center">
                      <div class="loading_statistics"></div>
                    </div>
                    <vuetable
                      ref="taskVuetable"
                      :api-url="taskVuetableItems.apiUrl"
                      :fields="taskVuetableItems.fields"
                      :http-fetch="taskVuetableHttpFetch"
                      :per-page="taskVuetableItems.perPage"
                      pagination-path="pagination"
                      class="table-hover"
                      @vuetable:pagination-data="onTaskVuetablePaginationData"
                      @vuetable:loading="loadingTable = true"
                      @vuetable:loaded="loadingTable = false"
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
  import 'vue-tree-halower/dist/halower-tree.min.css'
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
      this.getChartData();
    },
    data() {
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
            '#cccccc',
            '#1989fa',
            '#ff6600',
            '#009900',
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
                {
                  value: 0,
                  name: this.$t('statistics.view.invalid-scan')
                },
                {
                  value: 0,
                  name: this.$t('statistics.view.valid-scan')
                },
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
                {value: 0, name: this.$t('statistics.view.alarm-scan')},
                {value: 0, name: this.$t('permission-management.permission-control.pending-success')},
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
          color: ['#009900', '#ff6600', '#cccccc'],
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
        loadingTable:false,
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
          deviceId: null,
          userCategory: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },

        categoryLabel: '',

        siteData: [],
        allField: '',
        allDevice: '',
        preViewData: [],
        manualDeviceOptions: [],

        xYear: [],
        xQuarter: ['1', '2', '3', '4'],
        xMonth: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        monthLabel: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
        xWeek: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '29', '30', '31', '32', '33', '34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46', '47', '48', '49', '50', '51', '52'],
        xDay: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '29', '30', '31'],
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
          apiUrl: `${apiBaseUrl}/task/statistics/preview/detail`,
          fields: [
            {
              name: '__sequence',
              title: this.$t('knowledge-base.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'time',
              title : this.$t('statistics.view.periods'),
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
              title: this.$t('statistics.view.valid-scan-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.validScan;
              }
            },
            {
              name: 'scanStatistics',
              title: this.$t('statistics.view.valid-scan-rate-table'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.validScanRate.toFixed(1);
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
              name: 'judgeStatistics',
              title: this.$t('statistics.monitors.suspiction-judge-amount'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.suspictionJudge;
              }
            },
            {
              name: 'judgeStatistics',
              title: this.$t('statistics.monitors.suspiction-judge-rate'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.suspictionJudgeRate.toFixed(1);
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
              title: this.$t('statistics.view.hand-total'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.totalHandExamination;
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
      },

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

      getDateTimeFormat(datatime) {
        if (datatime == null) return '';
        return getDateTimeWithFormat(datatime, 'monitor');
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        // let checkedAll, checkedIds;
        // if (this.pageStatus === 'charts') {
        //   checkedAll = true;
        //   checkedIds = "";
        // } else {
        //   checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        //   checkedIds = this.$refs.taskVuetable.selectedTo;
        // }

        this.params = {
          'locale' : getLocale(),
          'isAll': true,
          'filter': {'filter': this.filter},
        };
        this.link = `task/statistics/preview/generate`;
        this.name = this.$t('menu.statistics-view');
        this.isModalVisible = true;
      },
      onExport() {
        // let checkedAll, checkedIds;
        // if (this.pageStatus === 'charts') {
        //   checkedAll = true;
        //   checkedIds = "";
        // } else {
        //   checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        //   checkedIds = this.$refs.taskVuetable.selectedTo;
        // }

        let params = {
          'locale' : getLocale(),
          'isAll': true,
          'filter': {'filter': this.filter},
        };
        let link = `task/statistics/preview/generate`;
        if (this.pageStatus !== 'charts' && checkedIds.length === 0) {

        } else {
          downLoadFileFromServer(link, params, 'Statistics-Preview', this.fileSelection);
          this.hideModal('model-export')
        }
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onPrintButton() {
        // let checkedAll, checkedIds;
        // if (this.pageStatus === 'charts') {
        //   checkedAll = true;
        //   checkedIds = "";
        // } else {
        //   checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        //   checkedIds = this.$refs.taskVuetable.selectedTo;
        // }

        let params = {
          'locale' : getLocale(),
          'isAll': true,
          'filter': {'filter': this.filter},
        };
        let link = `task/statistics/preview/generate`;

          printFileFromServer(link, params);

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

        getApiManager().post(`${apiBaseUrl}/task/statistics/preview/total`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;
          if (this.preViewData != null && this.preViewData.scanStatistics != null) {

            this.doublePieChartOptions.series[0].data[0].value = this.preViewData.scanStatistics.invalidScan;
            this.doublePieChartOptions.series[0].data[1].value = this.preViewData.scanStatistics.validScan;
            this.doublePieChartOptions.series[1].data[0].value = this.preViewData.scanStatistics.alarmScan;
            this.doublePieChartOptions.series[1].data[1].value = this.preViewData.scanStatistics.passedScan;

          } else {
            this.doublePieChartOptions.series[0].data[0].value = 0;
            this.doublePieChartOptions.series[0].data[1].value = 0;
            this.doublePieChartOptions.series[1].data[0].value = 0;
            this.doublePieChartOptions.series[1].data[1].value = 0;
          }
        }).catch((error) => {
        });

      },

      getChartData(){
        getApiManager().post(`${apiBaseUrl}/task/statistics/preview/chart`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          let chartData = response.data.data.detailedStatistics;

          let keyData = [];

          //this.xDay = Object.keys(chartData);

          switch (this.filter.statWidth) {
            case "hour":
              this.bar3ChartOptions.xAxis.data = this.xHour;
              break;
            case "day":
              this.bar3ChartOptions.xAxis.data = this.xDay;
              break;
            case "week":
              this.bar3ChartOptions.xAxis.data = this.xWeek;
              break;
            case "month":
              this.bar3ChartOptions.xAxis.data = this.xMonth;
              break;
            case "quarter":
              this.bar3ChartOptions.xAxis.data = this.xQuarter;
              break;
            case "year":
              this.bar3ChartOptions.xAxis.data = this.xYear;
              break;

          }

          //this.bar3ChartOptions.xAxis.data = this.xDay;
          if(this.filter.statWidth !== 'year') {
            for (let i = 0; i < this.bar3ChartOptions.xAxis.data.length; i++) {
              let key = this.bar3ChartOptions.xAxis.data[i];
              this.bar3ChartOptions.series[0].data[i] = 0;
              this.bar3ChartOptions.series[1].data[i] = 0;
              this.bar3ChartOptions.series[2].data[i] = 0;
              for (let j = 0; j < chartData.length; j++) {
                if (key === chartData[j].time) {
                  this.bar3ChartOptions.series[0].data[i] = chartData[j].scanStatistics.passedScan;
                  this.bar3ChartOptions.series[1].data[i] = chartData[j].scanStatistics.alarmScan;
                  this.bar3ChartOptions.series[2].data[i] = chartData[j].scanStatistics.invalidScan;
                }

              }
              //if()

              // if (chartData[key] != null && chartData[key].scanStatistics != null) {
              //   this.bar3ChartOptions.series[0].data[i] = chartData[key].scanStatistics.passedScan;
              //   this.bar3ChartOptions.series[1].data[i] = chartData[key].scanStatistics.alarmScan;
              //   this.bar3ChartOptions.series[2].data[i] = chartData[key].scanStatistics.invalidScan;
              // } else {
              //   this.bar3ChartOptions.series[0].data[i] = 0;
              //   this.bar3ChartOptions.series[1].data[i] = 0;
              //   this.bar3ChartOptions.series[2].data[i] = 0;
              // }
            }
          } else {
            for (let j = 0; j < chartData.length; j++) {
              this.bar3ChartOptions.xAxis.data.push(chartData[j].time);
              this.bar3ChartOptions.series[0].data[j] = chartData[j].scanStatistics.passedScan;
              this.bar3ChartOptions.series[1].data[j] = chartData[j].scanStatistics.alarmScan;
              this.bar3ChartOptions.series[2].data[j] = chartData[j].scanStatistics.invalidScan;
            }
          }

        }).catch((error) => {
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
        this.getChartData();
        this.$refs.taskVuetable.refresh();
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
      },
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page);
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
        for (let i = 0; i < Object.keys(data.detailedStatistics).length; i++) {
          let j = transformed.tKey[i];

          temp = data.detailedStatistics[j];

          transformed.data.push(temp);
        }
        return transformed
      },

      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

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
  .statistics-view {

    display: flex;
    flex-direction: column;

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

      .charts-part {

        display: flex;

        & > .row {

          flex-grow: 1;

          & > .col:nth-child(1) {
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

          & > .col:nth-child(2) {
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
  .overlay {
    /* position: relative; */
    /* top: 0; */
    /* left: 0; */
    width: 100%;
    height: 100%;
    z-index: 10;
    background-color: rgba(0,0,0,0.5);
    background-color: white;
  }
  .loading {
    display: inline-block;
    width: 30px;
    height: 30px;
    border: 2px solid rgba(#145388, 0.2);
    border-radius: 50%;
    border-top-color: #145388;
    animation: spin 1s ease-in-out infinite;
    -webkit-animation: spin 1s ease-in-out infinite;
    left: calc(50% - 15px);
    top: calc(50% - 15px);
    position: relative;
    z-index: 1;
  }
</style>
