<template>
  <div class="working-hours">
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
            <b-form-group :label="'业务类型'">
              <b-form-select v-model="filter.modeId" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'操作员'">
              <b-form-input v-model="filter.userName"></b-form-input>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.start-time')">
              <date-picker v-model="filter.startTime" type="datetime" format="MM/DD/YYYY HH:mm"
                           placeholder=""></date-picker>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.end-time')">
              <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
                           placeholder=""></date-picker>
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
            <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
            <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
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
              <div><span>D{{this.total.day}} {{this.total.hour}} : {{this.total.minute}} : {{this.total.second}}</span></div>
              <div><span>累计工时</span></div>
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
              <div><span>D{{this.scan.day}} {{this.scan.hour}} : {{this.scan.minute}} : {{this.scan.second}}</span></div>
              <div><span>扫描累计工时</span></div>
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
              <div><span>D{{this.judge.day}} {{this.judge.hour}} : {{this.judge.minute}} : {{this.judge.second}}</span></div>
              <div><span>判图累计工时</span></div>
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
              <div><span>D{{this.total.day}} {{this.total.hour}} : {{this.total.minute}} : {{this.total.second}}</span></div>
              <div><span>手检累计工时</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
    </b-row>
    <b-row class="mt-4 mb-2">
      <b-col class="d-flex justify-content-end align-items-center">
        <div>
          <b-button size="sm" class="ml-2" variant="info default" @click="onDisplaceButton()">
            <i class="icofont-exchange"></i>&nbsp;{{ $t('log-management.switch') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default" style="background-color: white">
            <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default" style="background-color: white">
            <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
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
                    <div class="value">{{this.scan.rate}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      扫描
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{this.judge.rate}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      判图
                    </div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">{{this.hand.rate}}%</div>
                    <div class="legend-name">
                      <div class="legend-icon"></div>
                      手检
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
            <h5 class="text-center my-4">人员工时统计</h5>
          </b-card-header>

          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>现场:</b></b-col>
            <b-col cols="11"><span>通道01, 通道02, 通道03, 通道04</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>安检仪:</b></b-col>
            <b-col cols="11"><span>安检仪001, 安检仪002, 安检仪003</span></b-col>
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
            <b-col cols="11"><span>{{filter.startTime}}-{{filter.endTime}}</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>统计步长:</b></b-col>
            <b-col cols="11"><span>{{filter.statWidth}}</span></b-col>
          </b-row>


                  <div class="table-wrapper table-responsive">
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
            ></vuetable-pagination-bootstrap>
          </div>
        </b-card>
      </b-col>
    </b-row>

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
  import {getApiManager} from "../../../api";
  import {responseMessages} from "../../../constants/response-messages";
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';

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

      this.getPreviewData();
    },
    data() {


      let doublePieChartData = {
        '扫描': {
          value: 800,
          color: '#1989fa'
        },
        '判图': {
          value: 300,
          color: '#ffd835',
        },
        '手检': {
          value: 200,
          color: '#ff0000',
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
            doublePieChartData['扫描'].color,
            doublePieChartData['判图'].color,
            doublePieChartData['手检'].color
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
                {value: doublePieChartData['扫描'].value, name: '扫描'},
                {value: doublePieChartData['判图'].value, name: '判图'},
                {value: doublePieChartData['手检'].value, name: '手检'},
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
            data: ['张三', '李四', '王五', '曹静', '孙俪', '小明', '小张', '小白', '小红', '小李', '小兰'],
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
          color: ['#ff0000', '#ffd835', '#1989fa'],
          series: [
            {
              name: '安检仪',
              type: 'bar',
              stack: '总量',
              data: [320, 302, 301, 334, 390, 330, 320, 100, 240, 290, 120]
            },
            {
              name: '判图',
              type: 'bar',
              stack: '总量',

              data: [120, 132, 101, 134, 90, 230, 210, 120, 320, 100, 30]
            },
            {
              name: '手检',
              type: 'bar',
              stack: '总量',
              data: [220, 182, 191, 234, 290, 330, 310, 300, 200, 20, 30]
            }
          ]
        },

        pageStatus: 'charts',

        filter: {
          modeId: null,
          userName: null,
          startTime: null,
          endTime: null,
          statWidth: 'hour',
        },
        preViewData: [],
        total: [],
        scan: [],
        judge:[],
        hand:[],

        graphData :[],

        // TODO: refactor temp table data to api mode
        tempData: {
          data: [1, 2, 3, 4, 5].map((e) => {

            return {
              id: e,
              period: '201-1104 00:00:00-20191104 00:59:59',
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
                    apiUrl: `${apiBaseUrl}/task/statistics/preview`,
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__sequence',
                            title: '序号',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'time',
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
                                if(scanStatistics==null)  return '';
                                return scanStatistics.totalScan;
                            }
                        },
                        {
                            name: 'scanStatistics',
                            title: '无效扫描量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (scanStatistics) => {
                                if(scanStatistics==null)  return '';
                                return scanStatistics.invalidScan;
                            }
                        },
                        {
                            name: 'scanStatistics',
                            title: '无效率',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (scanStatistics) => {
                                if(scanStatistics==null)  return '';
                                return scanStatistics.invalidScanRate;
                            }
                        },
                        {
                            name: 'judgeStatistics',
                            title: '判图量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (judgeStatistics) => {
                                if(judgeStatistics==null)  return '';
                                return judgeStatistics.totalJudge;
                            }
                        },
                        {
                            name: 'handExaminationStatistics',
                            title: '手检量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (handExaminationStatistics) => {
                                if(handExaminationStatistics==null)  return '';
                                return handExaminationStatistics.totalHandExamination;
                            }
                        },
                        {
                            name: 'judgeStatistics',
                            title: '无嫌疑量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (judgeStatistics) => {
                                if(judgeStatistics==null)  return '';
                                return judgeStatistics.noSuspictionJudge;
                            }
                        },
                        {
                            name: 'judgeStatistics',
                            title: '无嫌疑率',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (judgeStatistics) => {
                                if(judgeStatistics==null)  return '';
                                return judgeStatistics.noSuspictionJudgeRate;
                            }
                        },
                        {
                            name: 'handExaminationStatistics',
                            title: '无查获量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (handExaminationStatistics) => {
                                if(handExaminationStatistics==null)  return '';
                                return handExaminationStatistics.noSeizureHandExamination;
                            }
                        },
                        {
                            name: 'handExaminationStatistics',
                            title: '无查获率',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (handExaminationStatistics) => {
                                if(handExaminationStatistics==null)  return '';
                                return handExaminationStatistics.noSeizureHandExaminationRate;
                            }
                        },
                        {
                            name: 'handExaminationStatistics',
                            title: '查获量',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (handExaminationStatistics) => {
                                if(handExaminationStatistics==null)  return '';
                                return handExaminationStatistics.seizureHandExamination;
                            }
                        },
                        {
                            name: 'handExaminationStatistics',
                            title: '查获率',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (handExaminationStatistics) => {
                                if(handExaminationStatistics==null)  return '';
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
      }
    },
    methods: {

      getGraphData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/`, {
          filter : this.filter
        }).then((response) => {
          this.graphData = response.data.data;

        })
      },

      getPreviewData() {
        getApiManager().post(`${apiBaseUrl}/task/statistics/get-by-user-sum`, {
          filter: this.filter
        }).then((response) => {
          let message = response.data.message;
          this.preViewData = response.data.data;
          this.total.second = this.preViewData.totalSeconds%60;
          this.total.minute = ((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60)%60;
          this.total.hour = (((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60 -(((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60)%60))/60)%24;
          this.total.day = (((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60 -(((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60)%60))/60-(((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60 -(((this.preViewData.totalSeconds-this.preViewData.totalSeconds%60)/60)%60))/60)%24)/24;
          this.scan.rate = Math.floor(this.preViewData.scanSeconds/this.preViewData.totalSeconds*100);
          this.judge.rate = Math.floor(this.preViewData.judgeSeconds/this.preViewData.totalSeconds*100);
          this.hand.rate = Math.floor(this.preViewData.handSeconds/this.preViewData.totalSeconds*100);

          this.doublePieChartOptions.series.data[0].value = this.scan.rate;
          this.doublePieChartOptions.series.data[1].value = this.judge.rate;
          this.doublePieChartOptions.series.data[2].value = this.hand.rate;
          // this.doublePieChartOptions.series[1].data[1].value = this.preViewData.totalStatistics.scanStatistics.passedScan;
          //
          // if (this.filter.statWidth === 'year') {
          //   this.bar3ChartOptions.xAxis.data = this.xHour;
          // } else {
          //   this.xDay = Object.keys(this.preViewData.detailedStatistics);
          //   console.log(this.xDay);
          //   this.bar3ChartOptions.xAxis.data = this.xDay;
          //   for (let i = 0; i < this.xDay.length; i++) {
          //
          //     if (this.preViewData.detailedStatistics[i] != null) {
          //       this.bar3ChartOptions.series[0].data[i] = this.preViewData.detailedStatistics[i].scanStatistics.passedScan;
          //       this.bar3ChartOptions.series[1].data[i] = this.preViewData.detailedStatistics[i].scanStatistics.alarmScan;
          //       this.bar3ChartOptions.series[2].data[i] = this.preViewData.detailedStatistics[i].scanStatistics.invalidScan;
          //     }
          //   }
          // }

        });
      },

      onSearchButton() {
        console.log(this.filter.startTime);
        console.log(this.filter.endTime);
        this.getPreviewData();
        this.$refs.taskVuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          fieldId: null,
          deviceId: null,
          userCategory: null,
          userName: null,
          statWidth: 'hour',
          startTime: null,
          endTime: null
        };
        this.getPreviewData();
        this.$refs.taskVuetable.refresh();

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

        console.log(data.per_page);

        transformed.pagination = {
          total: data.total,
          per_page: data.per_page,
          current_page: data.current_page,
          last_page: data.last_page,
          from: data.from,
          to: data.to
        };

        //console.log(Object.keys(data.data.detailedStatistics).length);
        console.log(Object.keys(data.detailedStatistics).length);
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
  .working-hours {

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

    .btn-outline-info:hover {
      color: #122881;
    }

  }
</style>
