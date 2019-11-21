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
              <b-form-select plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'设备'">
              <b-form-select plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'时间'">
              <b-form-input  plain />
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'统计步长'">
              <b-form-select  plain />
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
              <div><span>D38 15 : 34 : 45</span></div>
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
              <div><span>D38 15 : 34 : 45</span></div>
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
              <div><span>D38 15 : 34 : 45</span></div>
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
              <div><span>D38 15 : 34 : 45</span></div>
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
            <i class="icofont-exchange"></i>&nbsp;切换
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
                    <div class="value">60%</div>
                    <div class="legend-name"><div class="legend-icon"></div>安检仪累计运行时长</div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">30%</div>
                    <div class="legend-name"><div class="legend-icon"></div>判图站累计运行时长</div>
                  </b-col>
                  <b-col class="legend-item">
                    <div class="value">10%</div>
                    <div class="legend-name"><div class="legend-icon"></div>手检站累计运行时长</div>
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
            <b-col cols="11"><span>张三, 李四, 王五</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>时间:</b></b-col>
            <b-col cols="11"><span>20191104 00:00:00-20191104 11:39:43</span></b-col>
          </b-row>
          <b-row class="no-gutters mb-2">
            <b-col cols="1"><b>统计步长:</b></b-col>
            <b-col cols="11"><span>小时</span></b-col>
          </b-row>


          <div class="table-wrapper table-responsive">
            <vuetable
              ref="taskVuetable"
              :api-mode="false"
              :data="tempData"
              data-path="data"
              pagination-path="pagination"
              :fields="taskVuetableItems.fields"
              :per-page="taskVuetableItems.perPage"
              :data-total="tempData.data.length"
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

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'v-chart': ECharts
    },
    mounted() {

    },
    data() {


      let doublePieChartData = {
        '安检仪累计运行时长': {
          value: 800,
          color: '#1989fa'
        },
        '判图站累计运行时长': {
          value: 300,
          color: '#ffd835',
        },
        '手检站累计运行时长': {
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
          apiUrl: `${apiBaseUrl}/...`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: '序号',
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:period',
              title: '时间段',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'totalScanAmount',
              title: '扫描总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'invalidScanAmount',
              title: '无效扫描量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'invalidityPercentage',
              title: '无效率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'monitorAmount',
              title: '判图量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'handCheckAmount',
              title: '手检量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'noSuspectAmount',
              title: '无嫌疑量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'noSuspectPercentage',
              title: '无嫌疑率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'noSeizedAmount',
              title: '无查获量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'noSeizedPercentage',
              title: '无查获率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'seizedAmount',
              title: '查获量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'seizedPercentage',
              title: '查获率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ],
          perPage: 5,
        },

      }
    },
    watch: {},
    methods: {
      onSearchButton() {

      },
      onResetButton() {

      },
      onRowClicked() {

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
    }
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
