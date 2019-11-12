<style lang="scss">

  .operating-log {
    .rounded-span {
      width: 20px;
      height: 20px;
      border-radius: 10px;
      cursor: pointer;
      background-color: #007bff;
    }
  }

</style>
<template>
  <div class="operating-log">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>


    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('log-management.operating-log.access-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-4">
              <b-col cols="6">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-ip')">
                      <b-form-input v-model="filter.accessIp"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <b-form-input v-model="filter.startingTime"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <b-form-input v-model="filter.endingTime"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-user')">
                      <b-form-input v-model="filter.accessUser"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="6" class="d-flex justify-content-end align-items-center">
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
                    ref="vueTable"
                    :api-mode="false"
                    :fields="vueTableItems.fields"
                    :data-manager="vueTableDataManager"
                    :per-page="vueTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onvueTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="vueTablePagination"
                    @vuetable-pagination:change-page="onvueTableChangePage"
                    :initial-per-page="vueTableItems.perPage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>

          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.operating-log.operating-log')">
        <b-row class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-4">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <b-form-input v-model="filter.startTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <b-form-input v-model="filter.endTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.client-ip')">
                      <b-form-input v-model="filter.clientIp"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating-result')">
                      <b-form-select v-model="filter.status" :options="statusSelectData" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded" >
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.object')">
                      <b-form-input v-model="filter.object"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.account-number')">
                      <b-form-input v-model="filter.accountNumber"></b-form-input>
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

            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="operatingLogTable"
                    :api-mode="false"
                    :fields="operatingLogTableItems.fields"
                    :data-manager="operatingLogDataManager"
                    pagination-path="pagination"
                    class="table-hover"
                    @vuetable:pagination-data="onOperatingLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="operatingLogPagination"
                    @vuetable-pagination:change-page="onOperatingLogTableChangePage"
                    :initial-per-page="operatingLogTableItems.perPage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>

          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>


  </div>
</template>
<script>
    import _ from 'lodash';
    import {apiBaseUrl} from "../../../constants/config";
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';


    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
        },
        data() {
            return {
                isExpanded:false,
                pageStatus: 'table',
                filter: {
                    startingTime: '',
                    endingTime:'',
                    accessIp: '',
                    accessUser:'',
                    accountNumber:'',
                    clientIp:'',
                    object:'',
                    startTime:'',
                    endTime: '',

                },
                statusSelectData: [
                    {value: null, text: this.$t('log-management.operating-log.status-all')},
                    {value: 'active', text: this.$t('log-management.operating-log.status-success')},
                    {value: 'inactive', text: this.$t('log-management.operating-log.status-failure')},
                ],
                vueTableItems: {
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'number',
                            title: this.$t('log-management.operating-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'access-time',
                            title: this.$t('log-management.operating-log.access-time'),
                            sortField: 'access-time',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'action',
                            title: this.$t('log-management.operating-log.action'),
                            sortField: 'action',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'accessIp',
                            title: this.$t('log-management.operating-log.access-ip'),
                            sortField: 'accessIp',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'accessUser',
                            title: this.$t('log-management.operating-log.access-user'),
                            sortField: 'accessUser',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                    ],
                    perPage: 5,

                },
                tempData: [
                    {
                        "number": 1,
                        "access-time": "00:00",
                        "action": "success",
                        "accessIp": "170.108.49.5",
                        "accessUser": "2139910831",
                    },
                    {
                        "number": 2,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 3,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 4,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 5,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 6,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 7,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                ],

                //second tab content
                operatingLogTableItems: {
                    perPage: 5,
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'number',
                            title: this.$t('log-management.operating-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'userId',
                            title: this.$t('log-management.operating-log.user-id'),
                            sortField: 'userId',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'userNumber',
                            title: this.$t('log-management.operating-log.user-number'),
                            sortField: 'userNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'clientIp',
                            title: this.$t('log-management.operating-log.client-ip'),
                            sortField: 'clientIp',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingObject',
                            title: this.$t('log-management.operating-log.object'),
                            sortField: 'operatingObject',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingNumber',
                            title: this.$t('log-management.operating-log.operating-number'),
                            sortField: 'operatingNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operating',
                            title: this.$t('log-management.operating-log.operating'),
                            sortField: 'operating',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingContent',
                            title: this.$t('log-management.operating-log.operating-content'),
                            sortField: 'operatingContent',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingResult',
                            title: this.$t('log-management.operating-log.operating-result'),
                            sortField: 'operatingResult',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'failureCode',
                            title: this.$t('log-management.operating-log.operating-failure-code'),
                            sortField: 'failureCode',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingTime',
                            title: this.$t('log-management.operating-log.operating-time'),
                            sortField: 'operatingTime',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        }
                    ],
                },
                operatingLogtempData: [
                    {
                        "number": 1,
                        "userId": "100",
                        "userNumber": "246",
                        "clientIp": "170.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 2,
                        "userId": "731",
                        "userNumber": "45",
                        "clientIp": "102.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 3,
                        "userId": "100",
                        "userNumber": "246",
                        "clientIp": "170.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 4,
                        "userId": "132",
                        "userNumber": "246",
                        "clientIp": "170.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 5,
                        "userId": "12",
                        "userNumber": "246",
                        "clientIp": "102.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 6,
                        "userId": "498",
                        "userNumber": "341",
                        "clientIp": "170.135.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                    {
                        "number": 7,
                        "userId": "369",
                        "userNumber": "100",
                        "clientIp": "151.108.49.5",
                        "operatingObject": "2139910831",
                        "operatingNumber": "2139910831",
                        "operating": "test",
                        "operatingContent": null,
                        "operatingResult": null,
                        "failureCode": null,
                        "operatingTime": null,
                    },
                ],
            }
        },
        methods: {
            vueTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.vueTable.makePagination(
                    local.length,
                    this.vueTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.vueTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onvueTablePaginationData(paginationData) {
                this.$refs.vueTablePagination.setPaginationData(paginationData);
            },
            onvueTableChangePage(page) {
                this.$refs.vueTable.changePage(page);
            },

            operatingLogDataManager(sortOrder, pagination) {
                let local = this.operatingLogtempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.operatingLogTable.makePagination(
                    local.length,
                    this.operatingLogTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.operatingLogTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onOperatingLogTablePaginationData(paginationData) {
                this.$refs.operatingLogPagination.setPaginationData(paginationData);
            },
            onOperatingLogTableChangePage(page) {
                this.$refs.operatingLogTable.changePage(page);
            },
        }
    }
</script>
