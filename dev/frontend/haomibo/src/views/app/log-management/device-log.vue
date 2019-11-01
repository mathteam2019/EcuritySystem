<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.device-log')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('log-management.hand-check')">
        <b-row v-if="!detailMode">
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>

                        <b-col >
                          <b-form-group :label="$t('log-management.device')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.timelimit')">
                            <b-form-select v-model="selectedTimelimit" :options="stateOptions" plain/>
                          </b-form-group>
                        </b-col>

                        <b-col></b-col>
                      </b-row>

                    </div>
                    <div class="align-self-center">
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.search') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.reset') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.import') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.export') }}</b-button>
                    </div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <vuetable
                      ref="vuetable"
                      :api-mode="false"
                      :fields="vuetableItems.fields"
                      :data-manager="dataManager"
                      :per-page="vuetableItems.perPage"
                      pagination-path="pagination"
                      @vuetable:pagination-data="onPaginationData"
                      class="table-striped"
                    >

                    </vuetable>
                    <vuetable-pagination-bootstrap
                      ref="pagination"
                      @vuetable-pagination:change-page="onChangePage"
                    ></vuetable-pagination-bootstrap>


                  </b-col>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.approved')">
        <b-row v-if="!detailMode">
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>

                        <b-col >
                          <b-form-group :label="$t('log-management.device')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.timelimit')">
                            <b-form-select v-model="selectedTimelimit" :options="stateOptions" plain/>
                          </b-form-group>
                        </b-col>

                        <b-col></b-col>
                      </b-row>

                    </div>
                    <div class="align-self-center">
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.search') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.reset') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.import') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.export') }}</b-button>
                    </div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <vuetable
                      ref="vuetable_approved"
                      :api-mode="false"
                      :fields="vuetableItems_approved.fields"
                      :data-manager="dataManager_approved"
                      :per-page="vuetableItems_approved.perPage"
                      pagination-path="pagination_approved"
                      @vuetable:pagination-data="onPaginationData_approved"
                      class="table-striped"
                    >

                    </vuetable>
                    <vuetable-pagination-bootstrap
                      ref="pagination_approved"
                      @vuetable-pagination:change-page="onChangePage_approved"
                    ></vuetable-pagination-bootstrap>


                  </b-col>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.remote')">
        <b-row v-if="!detailMode">
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>

                        <b-col >
                          <b-form-group :label="$t('log-management.device')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.timelimit')">
                            <b-form-select v-model="selectedTimelimit" :options="stateOptions" plain/>
                          </b-form-group>
                        </b-col>

                        <b-col></b-col>
                      </b-row>

                    </div>
                    <div class="align-self-center">
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.search') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('log-management.reset') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.import') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('log-management.export') }}</b-button>
                    </div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <vuetable
                      ref="vuetable_remote"
                      :api-mode="false"
                      :fields="vuetableItems_remote.fields"
                      :data-manager="dataManager_remote"
                      :per-page="vuetableItems_remote.perPage"
                      pagination-path="pagination_remote"
                      @vuetable:pagination-data="onPaginationData_remote"
                      class="table-striped"
                    >

                    </vuetable>
                    <vuetable-pagination-bootstrap
                      ref="pagination_remote"
                      @vuetable-pagination:change-page="onChangePage_remote"
                    ></vuetable-pagination-bootstrap>


                  </b-col>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
  </div>
</template>

<script>
    import _ from 'lodash';
    import InputTag from '../../../components/Form/InputTag';
    import vSelect from 'vue-select'
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
    import Vue2OrgTree from 'vue2-org-tree'
    import 'vue-select/dist/vue-select.css'

    export default {
        components: {
            'input-tag' : InputTag,
            'v-select' : vSelect,
            'vuetable' : Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap' : VuetablePaginationBootstrap,
            Vue2OrgTree
        },
        data () {
            return {
                selectedTimelimit:'',
                vuetableItems: {
                    perPage: 5,
                    fields: [
                        {
                            name: 'boot-time',
                            sortField: 'boot-time',
                            title: this.$t('log-management.boot-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scan-count',
                            sortField: 'scan-count',
                            title: this.$t('log-management.scan-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'alarm-count',
                            sortField: 'alarm-count',
                            title: this.$t('log-management.alarm-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'invalid-scan',
                            sortField: 'invalid-scan',
                            title: this.$t('log-management.invalid-scan'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'missing-alarm-rate',
                            sortField: 'missing-alarm-rate',
                            title: this.$t('log-management.missing-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'false-alarm-rate',
                            sortField: 'false-alarm-rate',
                            title: this.$t('log-management.false-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "boot-time": 1,
                        "scan-count": "0000",
                        "alarm-count": "2019-10-11",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 2,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-20",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 3,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 4,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 5,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 6,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 7,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                ],
                vuetableItems_approved: {
                    perPage: 5,
                    fields: [
                        {
                            name: 'boot-time',
                            sortField: 'boot-time',
                            title: this.$t('log-management.boot-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scan-count',
                            sortField: 'scan-count',
                            title: this.$t('log-management.scan-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'alarm-count',
                            sortField: 'alarm-count',
                            title: this.$t('log-management.alarm-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'invalid-scan',
                            sortField: 'invalid-scan',
                            title: this.$t('log-management.invalid-scan'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'missing-alarm-rate',
                            sortField: 'missing-alarm-rate',
                            title: this.$t('log-management.missing-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'false-alarm-rate',
                            sortField: 'false-alarm-rate',
                            title: this.$t('log-management.false-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "boot-time": 1,
                        "scan-count": "0000",
                        "alarm-count": "2019-10-11",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 2,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-20",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 3,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 4,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 5,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 6,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 7,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                ],
                vuetableItems_remote: {
                    perPage: 5,
                    fields: [
                        {
                            name: 'boot-time',
                            sortField: 'boot-time',
                            title: this.$t('log-management.boot-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scan-count',
                            sortField: 'scan-count',
                            title: this.$t('log-management.scan-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'alarm-count',
                            sortField: 'alarm-count',
                            title: this.$t('log-management.alarm-count'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'invalid-scan',
                            sortField: 'invalid-scan',
                            title: this.$t('log-management.invalid-scan'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'missing-alarm-rate',
                            sortField: 'missing-alarm-rate',
                            title: this.$t('log-management.missing-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'false-alarm-rate',
                            sortField: 'false-alarm-rate',
                            title: this.$t('log-management.false-alarm-rate'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "boot-time": 1,
                        "scan-count": "0000",
                        "alarm-count": "2019-10-11",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 2,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-20",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 3,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 4,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 5,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 6,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                    {
                        "boot-time": 7,
                        "scan-count": "0100",
                        "alarm-count": "2019-10-01",
                        "invalid-scan": "60",
                        "missing-alarm-rate":"30",
                        "false-alarm-rate":"10",
                    },
                ],
                stateOptions: [
                    {value: "all", text: this.$t('log-management.status-all')},
                    {value: "valid", text: this.$t('log-management.status-active')},
                    {value: "invalid", text: this.$t('log-management.status-inactive')},
                ],
                detailMode: false,
            }
        },
        methods: {
            onPaginationData(paginationData) {
                this.$refs.pagination.setPaginationData(paginationData);
            },
            onChangePage(page) {
                this.$refs.vuetable.changePage(page);
            },
            dataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }

                pagination = this.$refs.vuetable.makePagination(
                    local.length,
                    this.vuetableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.vuetableItems.perPage;

                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onPaginationData_approved(paginationData) {
                this.$refs.pagination_approved.setPaginationData(paginationData);
            },
            onChangePage_approved(page) {
                this.$refs.vuetable_approved.changePage(page);
            },

            dataManager_approved(sortOrder, pagination_approved) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }

                pagination_approved = this.$refs.vuetable_approved.makePagination(
                    local.length,
                    this.vuetableItems.perPage
                );

                let from = pagination_approved.from - 1;
                let to = from + this.vuetableItems_approved.perPage;

                return {
                    pagination_approved: pagination_approved,
                    data: _.slice(local, from, to)
                };
            },
            onPaginationData_remote(paginationData) {
                this.$refs.pagination_remote.setPaginationData(paginationData);
            },
            onChangePage_remote(page) {
                this.$refs.vuetable_remote.changePage(page);
            },

            dataManager_remote(sortOrder, pagination_remote) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }

                pagination_remote = this.$refs.vuetable_remote.makePagination(
                    local.length,
                    this.vuetableItems.perPage
                );

                let from = pagination_remote.from - 1;
                let to = from + this.vuetableItems_remote.perPage;

                return {
                    pagination_remote: pagination_remote,
                    data: _.slice(local, from, to)
                };
            },
            editRow(data) {
                console.log(data);
                this.detailMode = true;
            }
        }
    }
</script>
