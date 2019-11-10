<style lang="scss">
  .inline-label-form-section {
    .w-130-px {
      width: 130px !important;
    }
    .w-200-px {
      width: 200px !important;
    }
    input[type=time] {
      padding-left: 30px;
      position: relative;
      &:after {
        font-family: 'IcoFont' !important;
        content: "\eedc";
        position: absolute;
        left: 5px;
        font-size: 19px;
        color: #9c9999;
      }
    }
    input[type=number]:not(.full-width) {
      width: 60px;
    }
    div.front-icon {
      position: relative;
      display: flex;
      input {
        padding-left: 30px;
      }
      i {
        position: absolute;
        left: 5px;
        line-height: 39px;
        font-size: 19px;
        color: #9c9999;
      }
    }
  }
</style>
<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div v-if="pageStatus==='list'" class="h-100 d-flex flex-column">
        <b-row>
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.task')">
                  <b-form-input v-model="filterForm.task"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.status')">
                  <b-form-select v-model="filterForm.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.position')">
                  <b-form-select v-model="filterForm.position" :options="positionOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.device-classification')">
                  <b-form-select v-model="filterForm.deviceClassification" :options="deviceClassificationOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.device')">
                  <b-form-input v-model="filterForm.device"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="pendingListTable"
                :api-mode="false"
                :fields="pendingListTableItems.fields"
                :data-manager="pendingListTableDataManager"
                :per-page="pendingListTableItems.perPage"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                <span class="cursor-p text-primary"
                      @click="onAction('show',props.rowData)">{{ props.rowData.number}}</span>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pendingListTablePagination"
                :initial-per-page="pendingListTableItems.perPage"
                @vuetable-pagination:change-page="onBlackListTableChangePage"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>

      </div>
      <div v-if="pageStatus==='create'" class="form-section">
        <b-row>
          <b-col cols="6" class="pr-5">
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.task-number')}}&nbsp;<span
                class="text-danger">*</span></template>
              <b-form-input type="text"></b-form-input>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.time-task-name')}}&nbsp;<span
                class="text-danger">*</span></template>
              <b-form-input type="text"></b-form-input>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.create-time')}}&nbsp;<span
                class="text-danger">*</span></template>
              <div class="d-flex ">
                <div>
                  <b-form-radio-group stacked style="width: 60px">
                    <b-form-radio class="py-2 mb-1" value="first">{{$t('maintenance-management.time-task.by-time')}}
                    </b-form-radio>
                    <b-form-radio class="py-2 mb-1" value="second">{{$t('maintenance-management.time-task.by-day')}}
                    </b-form-radio>
                    <b-form-radio class="py-2 mb-1" value="third">{{$t('maintenance-management.time-task.by-week')}}
                    </b-form-radio>
                    <b-form-radio class="py-2 mb-1" value="fourth">{{$t('maintenance-management.time-task.by-month')}}
                    </b-form-radio>
                  </b-form-radio-group>
                </div>
                <div class="pl-4 inline-label-form-section">
                  <div class="d-flex align-items-center mb-1">
                    <span class=" px-2">{{$t('maintenance-management.time-task.every')}} </span>
                    <b-form-input type="time" class="w-130-px"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.hour')}} </span>
                  </div>
                  <div class="d-flex align-items-center mb-1">
                    <span class=" px-2">{{$t('maintenance-management.time-task.every')}} </span>
                    <b-form-input type="number"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.day')}} </span>
                    <b-form-input type="time" class="w-130-px"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.hour')}} </span>
                  </div>
                  <div class="d-flex align-items-center mb-1">
                    <span class=" px-2">{{$t('maintenance-management.time-task.every')}} </span>
                    <b-form-input type="number"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.week')}} </span>
                    <b-form-select class="w-130-px" :options="weekDayOptions" plain/>
                    <span class=" px-2">&nbsp;&nbsp;&nbsp; </span>
                    <b-form-input type="time" class="w-130-px"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.hour')}} </span>
                  </div>
                  <div class="d-flex align-items-center mb-1">
                    <span class=" px-2">{{$t('maintenance-management.time-task.every')}} </span>
                    <b-form-input type="number"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.month')}} </span>
                    <div class="front-icon">
                      <b-form-input type="number" class="w-130-px"></b-form-input>
                      <i class="icofont-calendar"></i></div>
                    <span class=" px-2">{{$t('maintenance-management.time-task.hour')}} </span>
                    <b-form-input type="time" class="w-130-px"></b-form-input>
                    <span class=" px-2">{{$t('maintenance-management.time-task.hour')}} </span>
                  </div>
                </div>
              </div>
            </b-form-group>
            <div class="pl-4 text-muted">({{$t('maintenance-management.time-task.guide-for-time-task')}})</div>
          </b-col>
          <b-col cols="6" class="pl-5 inline-label-form-section">
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.estimated-start-time')}}&nbsp;<span
                class="text-danger">*</span></template>
              <div class="d-flex align-items-center">
                <span class="px-2 pr-5">{{$t('maintenance-management.time-task.after-task-created')}}</span>
                <b-form-input type="number"></b-form-input>
                <span class="px-2">{{$t('maintenance-management.time-task.day')}}</span>
                <b-form-input type="time" class="w-130-px"></b-form-input>
                <span class="px-2">{{$t('maintenance-management.time-task.hour')}}</span>
              </div>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.number-of-execute')}}&nbsp;<span
                class="text-danger"></span></template>
              <b-form-input type="number" style="width: 100px"></b-form-input>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.timing-active-start-time')}}&nbsp;<span
                class="text-danger">*</span></template>
              <div class="d-flex">
                <div class="front-icon mr-2 w-200-px">
                  <b-form-input type="date" class="w-100"></b-form-input>
                  <i class="icofont-calendar"></i></div>
                <b-form-input class="w-200-px" type="time"></b-form-input>
              </div>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('maintenance-management.time-task.timing-active-end-time')}}&nbsp;<span
                class="text-danger">*</span></template>
              <div class="d-flex">
                <div class="front-icon mr-2 w-200-px">
                  <b-form-input type="date" class="w-100"></b-form-input>
                  <i class="icofont-calendar"></i></div>
                <b-form-input class="w-200-px" type="time"></b-form-input>
              </div>
            </b-form-group>
          </b-col>
          <b-col cols="12 text-right">
            <b-button variant="info default" @click="onAction('save-item')"><i class="icofont-save"></i>
              {{$t('permission-management.save')}}
            </b-button>
            <b-button variant="danger default" @click="onAction('delete-item')"><i class="icofont-bin"></i>
              {{$t('permission-management.delete')}}
            </b-button>
            <b-button variant="info default" @click="onAction('show-list')"><i class="icofont-long-arrow-left"></i>
              {{$t('permission-management.return')}}
            </b-button>
          </b-col>
        </b-row>

      </div>
    </b-card>

  </div>
</template>
<script>
    import _ from 'lodash';
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'

    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
        },
        data() {
            return {
                pageStatus: 'list',
                filterForm: {
                    task: null,
                    status: null,
                    position: null,
                    deviceClassification: null,
                    device: null
                },
                statusOptions: [
                    {value: null, text: this.$t('permission-management.all')},
                    {value: 'completed', text: this.$t('maintenance-management.process-task.completed')},
                    {value: 'processing', text: this.$t('maintenance-management.process-task.processing')},
                ],
                positionOptions: [
                    {value: null, text: this.$t('permission-management.all')},
                    {value: 'position-1', text: '清理'},
                    {value: 'position-2', text: '维修'}
                ],
                deviceClassificationOptions: [
                    {value: null, text: this.$t('permission-management.all')},
                    {value: 'classification-1', text: '分类1'},
                    {value: 'classification-2', text: '分类2'},
                    {value: 'classification-3', text: '分类3'},
                ],
                weekDayOptions: [
                    {value: null, text: this.$t('maintenance-management.all')},
                    {value: 1, text: this.$t('maintenance-management.week-1')},
                    {value: 2, text: this.$t('maintenance-management.week-2')},
                    {value: 3, text: this.$t('maintenance-management.week-3')},
                    {value: 4, text: this.$t('maintenance-management.week-4')},
                    {value: 5, text: this.$t('maintenance-management.week-5')},
                    {value: 6, text: this.$t('maintenance-management.week-6')},
                    {value: 7, text: this.$t('maintenance-management.week-7')},
                ],
                pendingListTableItems: {
                    perPage: 5,
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'no',
                            sortField: 'no',
                            title: this.$t('maintenance-management.process-task.no'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:number',
                            sortField: 'number',
                            title: this.$t('maintenance-management.process-task.number'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'task',
                            sortField: 'task',
                            title: this.$t('maintenance-management.process-task.task'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'status',
                            sortField: 'status',
                            title: this.$t('maintenance-management.process-task.status'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (value) => {
                                const dictionary = {
                                    "completed": `<span class="text-primary">${this.$t('maintenance-management.process-task.completed')}</span>`,
                                    "processing": `<span class="text-warning">${this.$t('maintenance-management.process-task.processing')}</span>`,
                                };
                                if (!dictionary.hasOwnProperty(value)) return '';
                                return dictionary[value];
                            }
                        },
                        {
                            name: 'category',
                            sortField: 'category',
                            title: this.$t('maintenance-management.process-task.category'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'position',
                            sortField: 'position',
                            title: this.$t('maintenance-management.process-task.position'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'deviceClassification',
                            sortField: 'deviceClassification',
                            title: this.$t('maintenance-management.process-task.device-classification'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'device',
                            sortField: 'device',
                            title: this.$t('maintenance-management.process-task.device'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'securityGuard',
                            sortField: 'securityGuard',
                            title: this.$t('maintenance-management.process-task.security-user'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'estimatedStartTime',
                            sortField: 'estimatedStartTime',
                            title: this.$t('maintenance-management.process-task.estimated-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'realStartTime',
                            sortField: 'realStartTime',
                            title: this.$t('maintenance-management.process-task.real-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "no": 1,
                        "number": "H201909200001",
                        "status": "processing",
                        "category": "清理",
                    },
                    {
                        "no": 2,
                        "number": "H201909200002",
                        "status": "completed",
                    },
                    {
                        "no": 3,
                        "number": "H201909200003",
                        "status": "processing",
                        "category": "清理",
                    },
                    {
                        "no": 4,
                        "number": "H201909200004",
                        "status": "processing",
                    },
                ],
            }
        },
        methods: {
            onSearchButton() {

            },
            onResetButton() {

            },
            onAction(value) {
                switch (value) {
                    case 'show-list':
                        this.pageStatus = 'list';
                        break;
                }
            },
            pendingListTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }

                pagination = this.$refs.pendingListTable.makePagination(
                    local.length,
                    this.pendingListTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.pendingListTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onBlackListTablePaginationData(paginationData) {
                this.$refs.pendingListTablePagination.setPaginationData(paginationData);
            },
            onBlackListTableChangePage(page) {
                this.$refs.pendingListTable.changePage(page);
            },
        }
    }
</script>





