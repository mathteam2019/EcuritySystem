<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.operating-log')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('log-management.access-log')">
        <b-row v-if="!detailMode">
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>

                        <b-col >
                          <b-form-group :label="$t('log-management.access-ip')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.start-time')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.end-time')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>
                        <b-col >
                          <b-form-group :label="$t('log-management.access-user')">
                            <b-form-input></b-form-input>
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

      <b-tab :title="$t('log-management.operating-log')">
        <b-row v-if="!detailMode">
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>

                        <b-col >
                          <b-form-group :label="$t('log-management.starting-time')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>

                        <b-col >
                          <b-form-group :label="$t('log-management.end-time')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>
                        <b-col >
                          <b-form-group :label="$t('log-management.client-ip')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>
                        <b-col >
                          <b-form-group :label="$t('log-management.operating-result')">
                            <b-form-select v-model="operatingResult" :options="resultOptions" plain/>
                          </b-form-group>
                        </b-col>
                        <b-col >
                          <b-form-group :label="$t('log-management.operating-object')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>
                        <b-col >
                          <b-form-group :label="$t('log-management.operating-account')">
                            <b-form-input></b-form-input>
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
                operatingResult:'',
                vuetableItems: {
                    perPage: 5,
                    fields: [
                        {
                            name: 'node-type',
                            sortField: 'node-type',
                            title: this.$t('log-management.node-type'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'node-name',
                            sortField: 'node-name',
                            title: this.$t('log-management.node-name'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'start-time',
                            sortField: 'start-time',
                            title: this.$t('log-management.start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'processing-time',
                            sortField: 'processing-time',
                            title: this.$t('log-management.processing-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "node-type": 1,
                        "node-name": "0000",
                        "start-time": "2019-10-11",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 2,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                ],
                vuetableItems_approved: {
                    perPage: 5,
                    fields: [
                        {
                            name: 'node-type',
                            sortField: 'node-type',
                            title: this.$t('log-management.node-type'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'node-name',
                            sortField: 'node-name',
                            title: this.$t('log-management.node-name'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'start-time',
                            sortField: 'start-time',
                            title: this.$t('log-management.start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'processing-time',
                            sortField: 'processing-time',
                            title: this.$t('log-management.processing-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "node-type": 1,
                        "node-name": "0000",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 2,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                    {
                        "node-type": 3,
                        "node-name": "0100",
                        "start-time": "2019-10-01",
                        "processing-time": "60",
                    },
                ],
                resultOptions: [
                    {value: "all", text: this.$t('log-management.status-all')},
                    {value: "success", text: this.$t('log-management.status-success')},
                    {value: "failure", text: this.$t('log-management.status-failure')},
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

            editRow(data) {
                console.log(data);
                this.detailMode = true;
            }
        }
    }
</script>
