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
            <b-form-group :label="'现场'">
              <b-form-select plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'安检仪'">
              <b-form-select plain/>
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'引导员'">
              <b-form-input />
            </b-form-group>
          </b-col>

          <b-col>
            <b-form-group :label="'时间'">
              <b-form-input />
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
              <b-form-select plain/>
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
          <b-button size="sm" class="ml-2" variant="info default" >
            <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
          </b-button>
          <b-button size="sm" class="ml-2" variant="info default" >
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
            <div><span class="font-weight-bold">3243</span></div>
            <div><span>手检</span></div>
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
                  <div><span>3243</span></div>
                  <div><span>无查获</span></div>
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
                  <div><span>80%</span></div>
                  <div><span>无查获率</span></div>
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
                  <div><span>3243</span></div>
                  <div><span>查获</span></div>
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
                  <div><span>20</span></div>
                  <div><span>报警率</span></div>
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
                  <div><span>7m6s</span></div>
                  <div><span>手检平均时长</span></div>
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
                  <div><span>20m8s</span></div>
                  <div><span>手检最高时长</span></div>
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
                  <div><span>3m0s</span></div>
                  <div><span>手检最低时长</span></div>
                </div>
              </div>
            </b-card>
          </b-col>
          <b-col></b-col>
        </b-row>
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
      <b-col v-if="pageStatus==='charts-1'" class="charts-part">
        <b-row>
          <b-col cols="4">
            <b-card>
              <b-img src="/assets/img/brand.png" class="w-100 h-100" style="object-fit: contain; object-position: center"/>
            </b-card>
          </b-col>
          <b-col cols="8">
            <b-card>

              <b-card-header>
                <h5>查获</h5>
              </b-card-header>

              <div class="w-100 flex-grow-1 d-flex flex-column ">
                <div class="bar-3-chart">
                  <v-chart :options="bar3ChartOptions" style="width: 100%" :autoresize="true"/>
                </div>
              </div>

            </b-card>
          </b-col>
        </b-row>
      </b-col>
      <b-col v-if="pageStatus==='table-1'">
        <b-card>
          <b-card-header>
            <h5 class="text-center my-4">毫米波人体查验手检统计</h5>
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

      return {
        isExpanded: false,
        bar3ChartOptions: {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
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
          color: ['#1989fa'],
          series: [
            {
              name: '安检仪',
              type: 'bar',
              stack: '总量',
              data: [320, 302, 301, 334, 390, 330, 320, 100, 240, 290, 120]
            }
          ]
        },

        pageStatus: 'charts-1',

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
        if (this.pageStatus === 'charts-1') {
          this.pageStatus = 'table-1';
        } else {
          this.pageStatus = 'charts-1';
        }
      },
    }
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

      .charts-part {

        display: flex;

        & > .row {

          flex-grow: 1;

          & > .col:nth-child(1) {
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
