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

    .btn-outline-info:hover {
      color: #122881;
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
                <b-form-select :options="onSiteOptions" plain/>
              </b-form-group>
            </b-col>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.security-device')">
                <b-form-select :options="[]" plain/>
              </b-form-group>
            </b-col>
            <b-col>
              <b-form-group :label="$t('statistics.evaluate-monitors.hand-checker')">
                <b-form-input plain/>
              </b-form-group>
            </b-col>
            <b-col cols="3">
              <b-form-group :label="$t('statistics.evaluate-monitors.step-size')">
                <b-form-select :options="[]" plain/>
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
            <b-form-group :label="$t('statistics.view.start-time')">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('statistics.view.end-time')">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>
	  
	  
          <b-col></b-col>
          <b-col></b-col>
          </b-row>
        </b-col>
        <b-col cols="4" class="d-flex justify-content-end align-items-center">
          <div>
            <b-button size="sm" class="ml-2" variant="info default">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
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
                <div><span>3243</span></div>
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
                <div><span>123</span></div>
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
                <div><span>15%</span></div>
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
                <div><span>500</span></div>
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
                <div><span>23%</span></div>
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
                <div><span>3243</span></div>
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
                <div><span>123</span></div>
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
                <div><span>15%</span></div>
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
                <div><span>500</span></div>
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
                <div><span>23%</span></div>
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
                <div><span>3243</span></div>
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
                <div><span>123</span></div>
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
                <div><span>15%</span></div>
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
                <div><span>500</span></div>
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
                <div><span>23%</span></div>
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
              <i class="icofont-exchange"></i>&nbsp;{{ $t('statistics.evaluate-monitors.displacement') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" style="background: white">
              <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" style="background: white">
              <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
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
                  <radial-progress-bar :diameter="156" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar :diameter="172" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
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
                  <v-chart :options="lineChartOptions" style="width: 100%; height: 300px" :autoresize="true"/>
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
                  <radial-progress-bar :diameter="156" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar :diameter="172" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
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
                  <v-chart :options="lineChartOptions" style="width: 100%; height: 300px" :autoresize="true"/>
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
                  <radial-progress-bar :diameter="156" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
                    误报
                  </radial-progress-bar>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-around align-items-center chart-type-2">
                  <radial-progress-bar :diameter="172" :strokeWidth="8" :completed-steps="30" :total-steps=100>
                    <span class="chart percent clearfix">30%</span>
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
                  <v-chart :options="lineChartOptions" style="width: 100%; height: 300px" :autoresize="true"/>
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

              <div class="table-wrapper table-responsive overflow-auto">
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
                  style="min-width: 1800px;"
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
                ></vuetable-pagination-bootstrap>
              </div>
            </b-card>
          </b-col>
        </b-row>
      </div>
      <b-row class="mt-3"></b-row>
    </div>
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

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'radial-progress-bar': RadialProgressBar,
      'v-chart': ECharts
    },
    mounted() {

    },
    data() {
      return {
        showTable: false,
        lineChartOptions: {
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
            data: ['0-1', '1-2', '2-3', '3-4', '4-5', '5-6', '6-7', '7-8', '8-9', '9-10', '10-11', '11-12']
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
              data: [120, 132, 101, 134, 90, 230, 210, 120, 132, 101, 134, 90]
            },
            {
              name: '漏报',
              type: 'line',
              symbolSize: 12,
              stack: '总量',
              data: [220, 182, 191, 234, 290, 330, 310, 191, 234, 290, 330, 310]
            }
          ],
          color: ['#ff0000', '#ff6600']
        },
        isExpanded: false,
        onSiteOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'pending-dispatch', text: this.$t('personal-inspection.task-pending-dispatch')},
          {value: 'dispatch', text: this.$t('personal-inspection.task-dispatched')},
          {value: 'while-review', text: this.$t('personal-inspection.while-review')},
          {value: 'reviewed', text: this.$t('personal-inspection.reviewed')},
          {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')},
        ],
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
          apiUrl: `${apiBaseUrl}/...`,
          fields: [
            {
              name: 'id',
              title: this.$t('personal-inspection.serial-number'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-1',
              title: '时间段',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-2',
              title: '手检总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-3',
              title: '误报总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-4',
              title: '误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-5',
              title: '漏报总量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-6',
              title: '漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-7',
              title: '手检（人工判图）量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-8',
              title: '人工判图误报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-9',
              title: '人工判图误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-10',
              title: '人工判图漏报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-11',
              title: '人工判图漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-12',
              title: '手检（智能判图）量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-13',
              title: '智能判图误报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-14',
              title: '智能判图误报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-15',
              title: '智能判图漏报量',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'toto-field-16',
              title: '智能判图漏报率',
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage: 5,
        }
      }
    },
    methods: {
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page)
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
      }
    }
  }
</script>
