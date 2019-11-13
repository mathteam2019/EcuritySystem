<template>
  <div class="statistics-view-page">
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
            <b-form-group :label="$t('personal-inspection.task-number')">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('personal-inspection.operation-mode')">
              <b-form-select v-model="filter.operationMode" :options="operationModeOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('personal-inspection.status')">
              <b-form-select v-model="filter.status" :options="statusOptions" plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('personal-inspection.on-site')">
              <b-form-select v-model="filter.onSite" :options="onSiteOptions" plain/>
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
            <b-form-group :label="$t('personal-inspection.user')">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="$t('personal-inspection.time')">
              <b-form-input></b-form-input>
            </b-form-group>
          </b-col>

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
          <b-button size="sm" class="ml-2" variant="outline-info default">
            <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default">
            <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>

    <b-row>
      <b-col>
        <b-card class="no-padding" style="background-color: #122881;">
          <div class="statistics-item type-1">
            <div>
              <b-img src="/assets/img/scan.svg"/>
            </div>
            <div>
              <div><span>2000</span></div>
              <div><span>扫描</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #1989fa;">
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
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #009900;">
              <b-img src="/assets/img/round_check.svg"/>
            </div>
            <div>
              <div><span>500</span></div>
              <div><span>通过</span></div>
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
              <div><span>500</span></div>
              <div><span>报警</span></div>
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
    </b-row>

    <b-row class="mt-4">
      <b-col>
        <b-card class="no-padding" style="background-color: #1989fa;">
          <div class="statistics-item type-1">
            <div>
              <b-img src="/assets/img/picture.svg"/>
            </div>
            <div>
              <div><span>1500</span></div>
              <div><span>判图</span></div>
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
              <div><span>1200</span></div>
              <div><span>无嫌疑</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #2bace2;">
          <div class="statistics-item type-1">
            <div>
              <b-img src="/assets/img/hand_check_icon.svg"/>
            </div>
            <div>
              <div><span>300</span></div>
              <div><span>手检</span></div>
            </div>
          </div>
        </b-card>
      </b-col>
      <b-col>
        <b-card class="no-padding" style="background-color: #fff;">
          <div class="statistics-item type-2">
            <div style="background-color: #009900;">
              <b-img src="/assets/img/glass_delete_icon.svg"/>
            </div>
            <div>
              <div><span>200</span></div>
              <div><span>无查获</span></div>
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
              <div><span>100</span></div>
              <div><span>查获</span></div>
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
          <b-button size="sm" class="ml-2" variant="outline-info default">
            <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="outline-info default">
            <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
          </b-button>
        </div>
      </b-col>
    </b-row>

    <div class="row bottom-part">
      <div v-if="pageStatus==='charts'" class="col charts-part">
        <b-row>
          <b-col>
            <b-card>

              <div class="w-100 h-100 d-flex align-items-center">

                  <v-chart :options="polar"/>

              </div>


            </b-card>
          </b-col>
          <b-col>
            <b-card>

              <h1>hello world</h1>

            </b-card>
          </b-col>
        </b-row>
      </div>
      <div v-if="pageStatus==='table'">
        <h1>world</h1>
      </div>
    </div>


  </div>
</template>


<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import Switches from 'vue-switches';


  import ECharts from 'vue-echarts'
  import 'echarts/lib/chart/line'
  import 'echarts/lib/component/polar'


  import {
    areaChartData,
    barChartData,
    doughnutChartData,
    lineChartData,
    pieChartData,
    polarAreaChartData,
    radarChartData,
    scatterChartData
  } from '../../../data/charts';

  import DoughnutChart from '../../../components/Charts/Doughnut';
  import BarChart from '../../../components/Charts/Bar';
  import _ from 'lodash';

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'doughnut-chart': DoughnutChart,
      'bar-chart': BarChart,
      'v-chart': ECharts
    },
    mounted() {

    },
    data() {

      let data = []

      for (let i = 0; i <= 360; i++) {
        let t = i / 180 * Math.PI
        let r = Math.sin(2 * t) * Math.cos(2 * t)
        data.push([r, i])
      }

      return {
        lineChartData,
        polarAreaChartData,
        areaChartData,
        scatterChartData,
        barChartData,
        radarChartData,
        pieChartData,
        doughnutChartData,


        polar: {
          title: {
            text: '极坐标双数值轴'
          },
          legend: {
            data: ['line']
          },
          polar: {
            center: ['50%', '54%']
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          angleAxis: {
            type: 'value',
            startAngle: 0
          },
          radiusAxis: {
            min: 0
          },
          series: [
            {
              coordinateSystem: 'polar',
              name: 'line',
              type: 'line',
              showSymbol: false,
              data: data
            }
          ],
          animationDuration: 2000
        },


        isExpanded: false,
        pageStatus: 'charts',

        filter: {
          operationMode: null,
          status: null,
          onSite: null
          // TODO: search filter
        },
        // TODO: select options
        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'security', text: this.$t('personal-inspection.security-instrument')},
          {value: 'security+hand', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
          {
            value: 'security+hand+device',
            text: this.$t('personal-inspection.security-instrument-and-hand-test-and-device')
          },
        ],
        statusOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'pending-dispatch', text: this.$t('personal-inspection.pending-dispatch')},
          {value: 'pending-review', text: this.$t('personal-inspection.pending-review')},
          {value: 'while-review', text: this.$t('personal-inspection.while-review')},
          {value: 'pending-inspection', text: this.$t('personal-inspection.pending-inspection')},
          {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')}
        ],
        onSiteOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'pending-dispatch', text: this.$t('personal-inspection.task-pending-dispatch')},
          {value: 'dispatch', text: this.$t('personal-inspection.task-dispatched')},
          {value: 'while-review', text: this.$t('personal-inspection.while-review')},
          {value: 'reviewed', text: this.$t('personal-inspection.reviewed')},
          {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')},
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
              title: this.$t('personal-inspection.serial-number'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:taskNumber',
              title: this.$t('personal-inspection.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:operationMode',
              title: this.$t('personal-inspection.operation-mode'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:status',
              title: this.$t('personal-inspection.status'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'onSite',
              title: this.$t('personal-inspection.on-site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'securityInstrument',
              title: this.$t('personal-inspection.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'guide',
              title: this.$t('personal-inspection.guide'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'scanStartTime',
              title: this.$t('personal-inspection.scan-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'scanEndTime',
              title: this.$t('personal-inspection.scan-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'judgementStation',
              title: this.$t('personal-inspection.judgement-station'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'judge',
              title: this.$t('personal-inspection.judge'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'judgementStartTime',
              title: this.$t('personal-inspection.judgement-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'judgementEndTime',
              title: this.$t('personal-inspection.judgement-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'handCheckStation',
              title: this.$t('personal-inspection.hand-check-station'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'handChecker',
              title: this.$t('personal-inspection.hand-checker'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'handCheckStartTime',
              title: this.$t('personal-inspection.hand-check-start-time'),
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
  .statistics-view-page {

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
          }

          & > .col:nth-child(2) {
            flex: 3 1 0;
          }

          .card {
            height: 100%;
          }
        }
      }

      .table-part {

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

