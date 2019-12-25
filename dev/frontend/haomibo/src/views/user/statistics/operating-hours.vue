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
            <b-form-group :label="'设备类型'">
              <b-form-select v-model="filter.deviceId" :options="categoryFilterData" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'设备'">
              <b-form-input v-model="filter.deviceCategoryId"/>
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
              <div><span>D{{totalData['day'].value}} {{totalData['hour'].value}}h: {{totalData['minute'].value}}m: {{totalData['second'].value}}s</span></div>
              <div><span>累计运行时长</span></div>
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
              <div><span>D{{scanData['day'].value}} {{scanData['hour'].value}}h: {{scanData['minute'].value}}m: {{scanData['second'].value}}s</span></div>
              <div><span>安检仪累计运行时长</span></div>
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
              <div><span>D{{judgeData['day'].value}} {{judgeData['hour'].value}}h: {{judgeData['minute'].value}}m: {{judgeData['second'].value}}s</span></div>
              <div><span>判图站累计运行时长</span></div>
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
              <div><span>D{{handData['day'].value}} {{handData['hour'].value}}h: {{handData['minute'].value}}m: {{handData['second'].value}}s</span>
              </div>
              <div><span>手检站累计运行时长</span></div>
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
          <b-button size="sm" class="ml-2" variant="outline-info default " @click="onExportButton()">
            <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default " @click="onPrintButton()">
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
                <h5>工时统计</h5>
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
                      安检仪累计运行时长
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{handData['rate'].value}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      手检站累计运行时长
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{judgeData['rate'].value}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      判图站累计运行时长
                    </div>
                  </b-col>
                </b-row>
              </div>

            </b-card>
          </b-col>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>工时统计</h5>
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
            <h5 class="text-center my-4">设备运行时长统计</h5>
          </b-card-header>

          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>现场:</b></b-col>
            <b-col cols="11">
              <span>{{this.allField}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>安检仪:</b></b-col>
            <b-col cols="11">
              <span>安检仪001, 安检仪002, 安检仪003</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>操作员类型:</b></b-col>
            <b-col cols="11"><span>引导员, 判图员, 手检员</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>操作员:</b></b-col>
            <b-col cols="11">
              <span v-if="filter.userName===null">张三, 李四, 王五</span>
              <span v-else>{{filter.userName}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>时间:</b></b-col>
            <b-col cols="11">
              <span>{{this.getDateTimeFormat(filter.startTime)}}-{{this.getDateTimeFormat(filter.endTime)}}</span>
            </b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>统计步长:</b></b-col>
            <b-col cols="11">
              <span v-if="filter.statWidth==='hour'">时</span>
              <span v-else-if="filter.statWidth==='day'">天</span>
              <span v-else-if="filter.statWidth==='week'">周</span>
              <span v-else-if="filter.statWidth==='month'">月</span>
              <span v-else-if="filter.statWidth==='quarter'">季度</span>
              <span v-else>年</span>
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
              @vuetable:pagination-data="onTaskVuetablePaginationData"
            >
              <template slot="period" slot-scope="props">
                        <span class="cursor-p text-primary" @click="onRowClicked(props.rowData)">
                          {{props.rowData.period}}
                        </span>
              </template>
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

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'v-chart': ECharts,
      'date-picker': DatePicker
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
                {value: doublePieChartData['安检仪累计运行时长'].value, name: '安检仪累计运行时长'},
                {value: doublePieChartData['判图站累计运行时长'].value, name: '判图站累计运行时长'},
                {value: doublePieChartData['手检站累计运行时长'].value, name: '手检站累计运行时长'},
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
            data: ['安检仪', '判图', '手检'],
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
          color: ['#1989fa', '#ff0000', '#ffd835'],
          series: [
            {
              name: '安检仪',
              type: 'bar',
              stack: '总量',
              data: [0]
            },
            {
              name: '判图',
              type: 'bar',
              stack: '总量',

              data: [0]
            },
            {
              name: '手检',
              type: 'bar',
              stack: '总量',
              data: [0]
            }
          ]
        },

        pageStatus: 'charts',

        filter: {
          deviceId: null,
          deviceCategoryId: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },

        siteData: [],
        allField: '',
        preViewData: [],
        graphData: [],
        total: [],
        scan: [],
        judge: [],
        hand: [],

        categoryData: [],
        categoryFilterData: [],

        statisticalStepSizeOptions: [
          {value: 'hour', text: "时"},
          {value: 'day', text: "天"},
          {value: 'week', text: "周"},
          {value: 'month', text: "月"},
          {value: 'quarter', text: "季度"},
          {value: 'year', text: "年"},
        ],

        totalData : {
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

        scanData : {
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
          'rate' : {
            value: 0
          }
        },

        judgeData : {
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
          'rate' : {
            value: 0
          }
        },

        handData : {
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
          'rate' : {
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
              name: 'id',
              title: '序号',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'name',
              title: '时间段',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'scanStatistics',
              title: '扫描总量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.totalScan;
              }
            },
            {
              name: 'scanStatistics',
              title: '无效扫描量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.invalidScan;
              }
            },
            {
              name: 'scanStatistics',
              title: '无效率',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStatistics) => {
                if (scanStatistics == null) return '';
                return scanStatistics.invalidScanRate;
              }
            },
            {
              name: 'judgeStatistics',
              title: '判图量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.totalJudge;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: '手检量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.totalHandExamination;
              }
            },
            {
              name: 'judgeStatistics',
              title: '无嫌疑量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.noSuspictionJudge;
              }
            },
            {
              name: 'judgeStatistics',
              title: '无嫌疑率',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStatistics) => {
                if (judgeStatistics == null) return '';
                return judgeStatistics.noSuspictionJudgeRate;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: '无查获量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.noSeizureHandExamination;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: '无查获率',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.noSeizureHandExaminationRate;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: '查获量',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.seizureHandExamination;
              }
            },
            {
              name: 'handExaminationStatistics',
              title: '查获率',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handExaminationStatistics) => {
                if (handExaminationStatistics == null) return '';
                return handExaminationStatistics.seizureHandExaminationRate;
              }
            },
          ],
          perPage: 5,
        },

      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
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
        }
        else {
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
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        if (this.pageStatus === 'charts')
          checkedAll = true;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
            'filter': {'filter': this.filter},
          'idList': checkedIds.join()
        };
        let link = `task/statistics/devicestatistics/generate`;
        downLoadFileFromServer(link, params, 'Statistics-OperatingHour');

      },

      onPrintButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        if (this.pageStatus === 'charts')
          checkedAll = true;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
            'filter': {'filter': this.filter},
          'idList': checkedIds.join()
        };
        let link = `task/statistics/devicestatistics/generate`;
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
          console.log(this.siteData);
          console.log(this.siteData[0].fieldDesignation);
          allFieldStr = allFieldStr + this.siteData[0].fieldDesignation;
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {
            //console.log(this.$refs.taskVuetable.selectedTo[i]);
            allFieldStr = allFieldStr + ", " + this.siteData[i].fieldDesignation;
            //console.log(str);
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

          let keyData = Object.keys(this.graphData.detailedStatistics);
          let xAxisChart = [];
          console.log(this.graphData.detailedStatistics.length);
          console.log(keyData);
          for (let i = 1; i < keyData.length; i++) {
            console.log(keyData[i]);
            let key = keyData[i];
            console.log(this.graphData.detailedStatistics[key].name);
            xAxisChart[i-1] = this.graphData.detailedStatistics[key].name;
            if(this.graphData.detailedStatistics[key].scanStatistics!=null) {
              this.bar3ChartOptions.series[0].data[i] = this.graphData.detailedStatistics[key].scanStatistics.workingSeconds;
            }
            else {
              this.bar3ChartOptions.series[0].data[i] = 0;
            }
            if(this.graphData.detailedStatistics[key].judgeStatistics!=null) {
              this.bar3ChartOptions.series[1].data[i] = this.graphData.detailedStatistics[key].judgeStatistics.workingSeconds;
            }
            else {
              this.bar3ChartOptions.series[1].data[i]=0;
            }
            if(this.graphData.detailedStatistics[key].handExaminationStatistics!=null) {
              this.bar3ChartOptions.series[2].data[i] = this.graphData.detailedStatistics[key].handExaminationStatistics.workingSeconds;
            }
            else {
              this.bar3ChartOptions.series[2].data[i]=0;
            }
          }
          console.log(xAxisChart);
          this.bar3ChartOptions.xAxis.data = xAxisChart;
        })
      },
      getPreviewData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-statistics-filter-by-device`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;

          // this.total = [];
          // this.scan = [];
          // this.judge = [];
          // this.hand = [];
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
        //this.$refs.taskVuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          deviceId: null,
          deviceCategoryId: null,
          statWidth: 'hour',
          startTime: null,
          endTime: null
        };

        //this.getGraphData();
        //this.getPreviewData();
        //this.$refs.taskVuetable.refresh();

      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
      },
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page)
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
          console.log(j);
          temp = data.detailedStatistics[j];
          transformed.data.push(temp)
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
