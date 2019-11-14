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
            <b-form-group :label="'现场'">
              <b-form-select v-model="filter.onSite" :options="onSiteOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'安检仪'">
              <b-form-select v-model="filter.securityDevice" :options="securityDeviceOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'引导员'">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'时间'">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

          <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols="8" v-if="isExpanded">
        <b-row>


          <b-col>
            <b-form-group :label="'统计步长'">
              <b-form-select v-model="filter.statisticalStepSize" :options="statisticalStepSizeOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col></b-col>
          <b-col></b-col>
          <b-col></b-col>
          <b-col></b-col>


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

    <b-row class="parameter-items">
      <b-col>
        <b-card class="no-padding w-100 h-100" style="background-color: #1989fa;">
          <div class="statistics-item type-3">
            <div style="">
              <b-img src="/assets/img/scan.svg"/>
            </div>
            <div><span>2000</span></div>
            <div><span>扫描总量</span></div>
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
                  <div><span>1500</span></div>
                  <div><span>有效扫描</span></div>
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
                  <div><span>75%</span></div>
                  <div><span>有效扫描率</span></div>
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
                  <div><span>500</span></div>
                  <div><span>无效扫描</span></div>
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
                  <div><span>25%</span></div>
                  <div><span>无效扫描率</span></div>
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
                  <div><span>1200</span></div>
                  <div><span>通过</span></div>
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
                  <div><span>80%</span></div>
                  <div><span>通过率</span></div>
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
                  <div><span>300</span></div>
                  <div><span>报警</span></div>
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
                  <div><span>20%</span></div>
                  <div><span>报警率</span></div>
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
            <i class="icofont-exchange"></i>&nbsp;切换
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default bg-white">
            <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default bg-white">
            <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
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
                <h5>扫描</h5>
              </b-card-header>
              <div class="w-100 flex-grow-1 d-flex flex-column justify-content-around">

                <div class="d-flex align-items-center justify-content-around">
                  <div>

                    <v-chart :options="doublePieChartOptions" :autoresize="true" style="width: 300px; height: 300px;"/>

                  </div>
                  <div class="legend-group">
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">无效扫描</div>
                      <div class="value">200</div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">报警</div>
                      <div class="value">200</div>
                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">通过</div>
                      <div class="value">500</div>

                    </div>
                    <div class="legend-item">
                      <div class="legend-icon"></div>
                      <div class="legend-name">有效扫描</div>
                      <div class="value">1500</div>
                    </div>
                  </div>
                </div>
              </div>

            </b-card>
          </b-col>
          <b-col>
            <b-card>

              <b-card-header>
                <h5>扫描历史</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div >

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

            <h5 class="text-center my-4">人体查验综合统计</h5>

          </b-card-header>

          <div class="flex-grow-1 ">
            <div class="container-fluid">
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
              <b-row class="no-gutters">

                <b-col cols>

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

                </b-col>

              </b-row>
            </div>
          </div>
        </b-card>

      </b-col>
    </b-row>


  </div>
</template>


<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from 'vuetable-2/src/components/Vuetable'
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
        '无效扫描': {
          value: 200,
          color: '#cccccc'
        },
        '有效扫描': {
          value: 1500,
          color: '#1989fa',
        },
        '报警': {
          value: 200,
          color: '#ff6600',
        },
        '通过': {
          value: 500,
          color: '#009900'
        },
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
                {value: doublePieChartData['无效扫描'].value, name: '无效扫描'},
                {value: doublePieChartData['有效扫描'].value, name: '有效扫描'},
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
                {value: doublePieChartData['报警'].value, name: '报警'},
                {value: doublePieChartData['通过'].value, name: '通过'},
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
            data: ['通过', '报警', '无效扫描'],
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
            data: ['0-1', '1-2', '2-3', '3-4', '4-5', '5-6', '6-7', '7-8', '8-9', '9-10', '10-11', '11-12'],
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
              name: '报警',
              type: 'bar',
              stack: '总量',
              data: [320, 302, 301, 334, 390, 330, 320, 100, 240, 290, 120, 300]
            },
            {
              name: '通过',
              type: 'bar',
              stack: '总量',

              data: [120, 132, 101, 134, 90, 230, 210, 120, 320, 100, 30, 80]
            },
            {
              name: '无效扫描',
              type: 'bar',
              stack: '总量',
              data: [220, 182, 191, 234, 290, 330, 310, 300, 200, 20, 30, 200]
            }
          ]
        },


        isExpanded: false,
        pageStatus: 'charts',

        filter: {
          onSite: null,
          securityDevice: null,
          operatorType: null,
          operator: null,
          time: null,
          statisticalStepSize: 'hour',

        },
        onSiteOptions: [
          {value: null, text: "全部"},
          {value: 'way_1', text: "通道1"},
          {value: 'way_2', text: "通道2"},
          {value: 'way_3', text: "通道3"},
        ],
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
              name: 'validScanAmount',
              title: '有效扫描量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'validityPercentage',
              title: '有效率',
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
              name: 'passAmount',
              title: '通过量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'passPercentage',
              title: '通过率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'reportAmount',
              title: '报警量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'reportPercentage',
              title: '报警率',
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
</style>

