<style lang="scss">

  .rounded-span{
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }


</style>
<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>


    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('log-management.device-log.security-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="filter.device"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="filter.user"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="filter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="filter.level"></b-form-input>
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
                    <b-form-group :label="$t('log-management.device-log.start-time')">
                      <b-form-input v-model="filter.startTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.end-time')">
                      <b-form-input v-model="filter.endTime"></b-form-input>
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
                    ref="securityLogTable"
                    :api-mode="false"
                    :fields="securityLogTableItems.fields"
                    :data-manager="securityLogTableDataManager"
                    :per-page="securityLogTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onSecurityLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="securityLogPagination"
                    @vuetable-pagination:change-page="onsecurityLogTableChangePage"
                    :initial-per-page="securityLogTableItems.perPage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>



          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.device-log.decision-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="filter.device"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="filter.user"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="filter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="filter.level"></b-form-input>
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
                    <b-form-group :label="$t('log-management.device-log.start-time')">
                      <b-form-input v-model="filter.startTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.end-time')">
                      <b-form-input v-model="filter.endTime"></b-form-input>
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
                    ref="decisionLogTable"
                    :api-mode="false"
                    :fields="decisionLogTableItems.fields"
                    :data-manager="decisionLogTableDataManager"
                    :per-page="decisionLogTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="ondecisionLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="decisionLogPagination"
                    @vuetable-pagination:change-page="ondecisionLogTableChangePage"
                    :initial-per-page="decisionLogTableItems.perPage"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>



          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.device-log.hand-check-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="filter.device"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="filter.user"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="filter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="filter.level"></b-form-input>
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
                    <b-form-group :label="$t('log-management.device-log.start-time')">
                      <b-form-input v-model="filter.startTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.end-time')">
                      <b-form-input v-model="filter.endTime"></b-form-input>
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
                    ref="handCheckLogTable"
                    :api-mode="false"
                    :fields="handCheckLogTableItems.fields"
                    :data-manager="handCheckLogTableDataManager"
                    :per-page="handCheckLogTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onhandCheckLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="handCheckLogPagination"
                    @vuetable-pagination:change-page="onhandCheckLogTableChangePage"
                    :initial-per-page="handCheckLogTableItems.perPage"
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

    import {apiBaseUrl} from "../../../constants/config";
    import _ from "lodash";
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
                    startTime: '',
                    endTime:'',
                    device:'',
                    user:'',
                    category:'',
                    level:'',

                },

                securityLogTableItems: {
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'number',
                            title: this.$t('log-management.device-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'device',
                            title: this.$t('log-management.device-log.device'),
                            sortField: 'device',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'deviceNumber',
                            title: this.$t('log-management.device-log.device-number'),
                            sortField: 'deviceNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'user',
                            title: this.$t('log-management.device-log.user'),
                            sortField: 'user',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'category',
                            title: this.$t('log-management.device-log.category'),
                            sortField: 'category',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'level',
                            title: this.$t('log-management.device-log.level'),
                            sortField: 'level',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'content',
                            title: this.$t('log-management.device-log.content'),
                            sortField: 'content',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingTime',
                            title: this.$t('log-management.device-log.operating-time'),
                            sortField: 'operatingTime',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        }
                    ],
                    perPage: 5,
                },
                tempData: [
                    {
                        "number": 1,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 2,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 3,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 4,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 5,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 6,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                    {
                        "number": 7,
                        "device": "00:00",
                        "deviceNumber": "009",
                        "user": "test",
                        "category": "2139910831",
                        "level": null,
                        "content": null,
                        "operatingTime": "00:00:00",
                    },
                ],

                //second tab
                decisionLogTableItems: {
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'number',
                            title: this.$t('log-management.device-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'device',
                            title: this.$t('log-management.device-log.device'),
                            sortField: 'device',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'deviceNumber',
                            title: this.$t('log-management.device-log.device-number'),
                            sortField: 'deviceNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'user',
                            title: this.$t('log-management.device-log.user'),
                            sortField: 'user',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'category',
                            title: this.$t('log-management.device-log.category'),
                            sortField: 'category',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'level',
                            title: this.$t('log-management.device-log.level'),
                            sortField: 'level',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'content',
                            title: this.$t('log-management.device-log.content'),
                            sortField: 'content',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingTime',
                            title: this.$t('log-management.device-log.operating-time'),
                            sortField: 'operatingTime',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        }
                    ],
                    perPage: 5,
                },
                //third tab
                handCheckLogTableItems: {
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'number',
                            title: this.$t('log-management.device-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'device',
                            title: this.$t('log-management.device-log.device'),
                            sortField: 'device',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'deviceNumber',
                            title: this.$t('log-management.device-log.device-number'),
                            sortField: 'deviceNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'user',
                            title: this.$t('log-management.device-log.user'),
                            sortField: 'user',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'category',
                            title: this.$t('log-management.device-log.category'),
                            sortField: 'category',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'level',
                            title: this.$t('log-management.device-log.level'),
                            sortField: 'level',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'content',
                            title: this.$t('log-management.device-log.content'),
                            sortField: 'content',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingTime',
                            title: this.$t('log-management.device-log.operating-time'),
                            sortField: 'operatingTime',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        }
                    ],
                    perPage: 5,
                },
            }
        },
        methods: {
            securityLogTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.securityLogTable.makePagination(
                    local.length,
                    this.securityLogTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.securityLogTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onSecurityLogTablePaginationData(paginationData) {
                this.$refs.securityLogPagination.setPaginationData(paginationData);
            },
            onsecurityLogTableChangePage(page) {
                this.$refs.securityLogTable.changePage(page);
            },

            //second tab
            decisionLogTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.decisionLogTable.makePagination(
                    local.length,
                    this.decisionLogTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.decisionLogTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            ondecisionLogTablePaginationData(paginationData) {
                this.$refs.decisionLogPagination.setPaginationData(paginationData);
            },
            ondecisionLogTableChangePage(page) {
                this.$refs.decisionLogTable.changePage(page);
            },

            //third tab
            handCheckLogTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.handCheckLogTable.makePagination(
                    local.length,
                    this.handCheckLogTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.handCheckLogTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onhandCheckLogTablePaginationData(paginationData) {
                this.$refs.handCheckLogPagination.setPaginationData(paginationData);
            },
            onhandCheckLogTableChangePage(page) {
                this.$refs.handCheckLogTable.changePage(page);
            },
        }
    }
</script>
