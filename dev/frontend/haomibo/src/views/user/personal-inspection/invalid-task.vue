<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-card class="main-without-tab" v-if="pageStatus === 'table'">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input v-model="filter.taskNumber"></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.operation-mode')">
                  <b-form-select v-model="filter.mode" :options="operationModeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.status')">
                  <b-form-select v-model="filter.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input v-model="filter.userName"></b-form-input>
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
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="taskVuetable"
                :api-url="taskVuetableItems.apiUrl"
                :fields="taskVuetableItems.fields"
                :http-fetch="taskVuetableHttpFetch"
                :per-page="taskVuetableItems.perPage"
                pagination-path="pagination"
                class="table-hover"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="taskNumber" slot-scope="props">
                    <span class="cursor-p text-primary" @click="onRowClicked(props.rowData.taskId)">
                      {{props.rowData.taskNumber}}
                    </span>
                </template>
                <template slot="scanImage" slot-scope="props">
                  <b-img :src="props.rowData.serScan.scanImage.imageUrl" class="operation-icon"/>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="filter.mode==null">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="filter.mode==='security'">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="filter.mode==='security+hand'">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                  </div>
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
    </b-card>

    <div v-if="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col cols="3">
          <b-card class="pt-4 h-100">
            <b-row class="mb-1">
              <b-col>
                <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                <b-img src="/assets/img/monitors_icon.svg" class="operation-icon ml-2"/>
                <b-img src="/assets/img/mobile_icon.svg" class="operation-icon ml-2"/>
              </b-col>
              <b-col class="text-right icon-container">
                <span><i class="icofont-star"></i></span>
                <span><i class="icofont-search-user"></i></span>
                <span><i class="icofont-female"></i></span>
              </b-col>
            </b-row>

            <b-row class="mb-4">
              <b-col>
                <b-img src="/assets/img/scan-rl.gif" fluid-grow></b-img>
              </b-col>
              <b-col>
                <b-img src="/assets/img/scan-lr.gif" fluid-grow></b-img>
              </b-col>
            </b-row>

            <b-row class="mb-2">
              <b-col class="control-group">
                <div class="control-btn-wrapper">

                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>


                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>

                <div class="switch-wrapper">
                  <div class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" theme="custom" color="info"></switches>
                  </div>
                </div>
              </b-col>
            </b-row>


          </b-card>
        </b-col>
        <b-col cols="9">
          <b-card class="h-100 d-flex flex-column right-card">
            <div class="history-chart">
              <div>

                <div class="part">
                  <div class="left">
                    <div>开始</div>
                  </div>
                  <div class="right">
                    <div>Start</div>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>扫描</div>
                    <div>张三</div>
                  </div>
                  <div class="right">
                    <div>Scanning</div>
                    <div>zhang san</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>判图</div>
                    <div>李四</div>
                  </div>
                  <div class="right">
                    <div>Decision diagram</div>
                    <div>Li si</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>查验</div>
                    <div>王五</div>
                  </div>
                  <div class="right">
                    <div>Inspection</div>
                    <div>Wang wu</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>结束</div>
                  </div>
                  <div class="right">
                    <div>End</div>
                  </div>
                </div>

              </div>

            </div>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.taskNumber}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.field.fieldDesignation}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.serScan.scanDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.serScan.scanImage.imageLabel}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scanned-image')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.serScan.scanImage.imageUrl}}</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <div>
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon ml-2"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon ml-2"/>
                  </div>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.serScan.scanStartTime}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-end-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.serScan.scanEndTime}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.atr-conclusion')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.taskNumber}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.foot-alarm')}}
                  </template>
                  <label>{{showPage.taskNumber}}</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.guide')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.taskNumber}}</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row class="flex-grow-1 d-flex align-items-end">
              <b-col class="text-right">
                <b-button size="sm" variant="info default" @click="pageStatus='table'">
                  <i class="icofont-long-arrow-left"></i>
                  {{ $t('personal-inspection.return') }}
                </b-button>
              </b-col>
            </b-row>

          </b-card>
        </b-col>
      </b-row>
    </div>
  </div>
</template>

<style lang="scss">
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

  .operation-icon {
    width: 24px;
    height: 24px;
  }

  .icon-container {
    font-size: 20px;

    .icofont-star {
      color: #ffe400;
    }

    .icofont-search-user {
      color: #ff9c0e;
    }

    .icofont-female {
      color: #fe687f;
    }
  }

  .control-group {
    display: flex;
    align-items: flex-start;

    .control-btn-wrapper {
      display: flex;
      flex-grow: 1;
      flex-wrap: wrap;

      .control-btn {
        width: calc(100% / 6);
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 24px;

        img {
          $size: 40px;
          width: $size;
          height: $size;
          margin-bottom: 6px;
        }

        span {
          display: block;
        }
      }
    }

    .switch-wrapper {
      width: 60px;
      height: 40px;
      display: flex;
      align-items: center;

      .separator {
        border: 0;
        width: 1px;
        height: 30px;
        background: #1e9dd2;
        flex-shrink: 0;
      }

      .switch {
        .vue-switcher {
          display: flex;
          height: 100%;
          margin: 0;
          transform: scale(0.8);
        }
      }
    }

    @media screen and (max-width: 1700px) {

      .control-btn-wrapper {
        .control-btn {
          img {
            $size: 28px;
            width: $size !important;
            height: $size !important;
          }
        }
      }
      .switch-wrapper {
        height: 28px;

        .separator {
          height: 28px;
        }
      }

    }
  }


  .history-chart {

    $ratio: 12.8;

    width: 100%;
    padding-bottom: 100% / $ratio;
    position: relative;

    margin-bottom: 24px;

    & > :first-child {
      left: 0;
      height: 100%;
      position: absolute;
      top: 0;
      width: 100%;

      background: url("/assets/img/history_chart.png") no-repeat;
      background-size: contain;


      $elements: 5;
      @for $i from 0 to $elements {
        .part:nth-child(#{$i + 1}) {
          position: absolute;
          top: 25%;
          bottom: 25%;
          left: 2% + 20% * $i;
          width: 20% - 4%;
          display: flex;
          color: white;
          align-items: center;
          justify-content: space-between;

          $date-color: #0c70ab;

          .top-date {
            color: $date-color;
            position: absolute;
            top: 104%;
            left: -6%;
          }

          .bottom-date {
            color: $date-color;
            position: absolute;
            bottom: 104%;
            right: 2%;
          }
        }
      }

    }


  }

</style>

<script>

    import {apiBaseUrl} from "../../../constants/config";
    import Vuetable from '../../../components/Vuetable2/Vuetable'
    import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';
    import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
    import Switches from 'vue-switches';

    const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
            'switches': Switches
        },
        mounted() {

        },
        data() {
            return {
                isExpanded: false,
                pageStatus: 'table',
                filter: {
                    taskNumber: null,
                    mode: null,
                    fieldId: null,
                    userName: null,
                    startTime: null,
                    endTime: null
                    // TODO: search filter
                },

                showPage: [],

                showPage1: { // modify page
                    selectedOrg: {},
                    taskNumber: '',
                    onSite: '',
                    machine: '',
                    sex: '',
                    scanImage: '',
                    operationMode: '',
                    status: '',
                    guide: '',
                    atrCon: '',
                    footAlarm: '',
                    scanStartTime: '',
                    scanEndTime: '',
                    dispatchTimeout: '',
                    judgementStation: '',
                    judgementType: '',
                    judgement: '',
                    judgementTimeout: '',
                    judgementStartTime: '',
                    judgementStationIde: '',
                    judgementEndTime: '',
                    judge: '',
                    handCheckStation: '',
                    handCheckStartTime: '',
                    handChecker: '',

                },
                // TODO: select options
                operationModeOptions: [
                    {value: null, text: this.$t('personal-inspection.all')},
                    {value: 'security', text: this.$t('personal-inspection.security-instrument')},
                    {value: 'security+hand', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
                ],
                statusOptions: [
                    {value: null, text: this.$t('personal-inspection.all')},
                    {value: 'pending_dispatch', text: this.$t('personal-inspection.pending-dispatch')},
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

                taskVuetableItems: {
                    apiUrl: `${apiBaseUrl}/task/invalid-task/get-by-filter-and-page`,
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'taskId',
                            title: this.$t('personal-inspection.serial-number'),
                            sortField: 'id',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:taskNumber',
                            title: this.$t('personal-inspection.task-number'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:scanImage',
                            title: this.$t('personal-inspection.image'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:mode',
                            title: this.$t('personal-inspection.operation-mode'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'field',
                            title: this.$t('personal-inspection.on-site'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (field) => {
                                if (field == null) return '';
                                return field.fieldDesignation;
                            }
                        },
                        {
                            name: 'serScan',
                            title: this.$t('personal-inspection.security-instrument'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (serScan) => {
                                if (serScan == null) return '';
                                return serScan.scanDevice.deviceName;
                            }
                        },
                        {
                            name: 'serScan',
                            title: this.$t('personal-inspection.guide'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (serScan) => {
                                if (serScan == null) return '';
                                return serScan.scanPointsman.userName;
                            }
                        },
                        {
                            name: 'serScan',
                            title: this.$t('personal-inspection.scan-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (serScan) => {
                                if (serScan == null) return '';
                                return serScan.scanStartTime;
                            }
                        },
                        {
                            name: 'serScan',
                            title: this.$t('personal-inspection.scan-end-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (serScan) => {
                                if (serScan == null) return '';
                                return serScan.scanEndTime;
                            }
                        },
                    ],
                    perPage: 5,
                },
                power: true

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
            onRowClicked(taskNumber) {

                // call api
                getApiManager()
                    .post(`${apiBaseUrl}/task/invalid-task/get-one`, {
                        'taskId': taskNumber,
                    })
                    .then((response) => {
                        let message = response.data.message;
                        this.showPage = response.data.data;

                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-activated-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });
                        }
                    })
                    .catch((error) => {
                    });


                this.pageStatus = 'show';
            },

            onSearchButton() {
                this.$refs.taskVuetable.refresh();
            },
            onResetButton() {

                this.filter = {
                    taskNumber: null,
                    mode: null,
                    fieldId: null,
                    userName: null,
                    startTime: null,
                    endTime: null
                };
                this.$refs.taskVuetable.refresh();
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

                transformed.data = [];
                let temp;
                for (let i = 0; i < data.data.length; i++) {
                    temp = data.data[i];
                    transformed.data.push(temp)
                }

                return transformed

            },

            taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

                return getApiManager().post(apiUrl, {
                    currentPage: httpOptions.params.page,

                    filter: {
                        taskNumber: this.filter.taskNumber,
                        mode: this.filter.mode,
                        fieldId: this.filter.fieldId,
                        userName: this.filter.userName,
                        startTime: null,
                        endTime: null

                    },

                    perPage: this.taskVuetableItems.perPage,
                });
            },
            onTaskVuetablePaginationData(paginationData) {
                this.$refs.taskVuetablePagination.setPaginationData(paginationData)
            },
            onTaskVuetableChangePage(page) {
                this.$refs.taskVuetable.changePage(page)
            }
        }
    }
</script>
