<style lang="scss">
  .rounded-span {
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
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('knowledge-base.task-number')">
                  <b-form-input v-model="filterForm.number"></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.operating-mode')">
                  <b-form-select v-model="filterForm.mode" :options="modeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.task-result')">
                  <b-form-select v-model="filterForm.result" :options="resultTypeOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('knowledge-base.site')">
                  <b-form-select v-model="filterForm.site" :options="siteOptions" plain/>
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
                <b-form-group :label="$t('knowledge-base.seized-item')">
                  <b-form-input v-model="filterForm.seizedItem"></b-form-input>
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
                <div slot="operating" slot-scope="props">
                  <b-button
                    size="sm"
                    variant="success default btn-square">
                    <i class="icofont-check-alt"></i>
                  </b-button>
                  <b-button
                    size="sm"
                    variant="danger default btn-square">
                    <i class="icofont-ban"></i>
                  </b-button>
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
        isExpanded: false,
        filterForm: {
          number: null,
          mode: null,
          result: null,
          seizedItem: null,
          site: null,
        },
        modeOptions: [
          {value: '1', text: this.$t('knowledge-base.security-instrument')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test-and-device')},
        ],
        siteOptions: [
          {value: 'male', text: this.$t('knowledge-base.all')},
          {value: 'female', text: this.$t('knowledge-base.airport')},
          {value: 'unknown', text: this.$t('knowledge-base.port')},
          {value: 'unknown', text: this.$t('knowledge-base.land-border')},
        ],
        resultTypeOptions: [
          {value: 'male', text: this.$t('knowledge-base.no-suspect')},
          {value: 'female', text: this.$t('knowledge-base.seized')},
          {value: 'unknown', text: this.$t('knowledge-base.no-seized')},
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
              title: this.$t('knowledge-base.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('knowledge-base.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archive',
              sortField: 'archive',
              title: this.$t('knowledge-base.image'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('knowledge-base.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1": `<span class="text-success ">${this.$t('knowledge-base.no-suspect')}</span>`,
                  "2": `<span class="text-danger">${this.$t('knowledge-base.seized')}</span>`,
                  "3": `<span class="text-warning">${this.$t('knowledge-base.no-seized')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }

            },
            {
              name: 'manufacturer',
              sortField: 'manufacturer',
              title: this.$t('knowledge-base.site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'channel',
              sortField: 'channel',
              title: this.$t('knowledge-base.channel'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'origin-no',
              sortField: 'origin-no',
              title: this.$t('knowledge-base.seized-item'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center btn-actions'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "1",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "2",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "MW毫米波安检仪",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "华为M6平板",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 5,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 6,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 7,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
        ],
      }
    },
    methods: {
      onSearchButton() {

      },
      onResetButton() {

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


