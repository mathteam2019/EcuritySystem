<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.document-management')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('device-management.file-list')">
        <b-row>
          <b-col cols="12" class="mb-4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col class="d-flex">
                    <div class="flex-grow-1">

                      <b-row>
                        <b-col>
                          <b-form-group :label="$t('device-management.file-name')">
                            <b-form-input></b-form-input>
                          </b-form-group>
                        </b-col>
                        <b-col>
                          <b-form-group :label="$t('device-management.active')">
                            <v-select :options="stateOptions" plain/>
                          </b-form-group>
                        </b-col>
                        <b-col>
                          <b-form-group :label="$t('device-management.device-classify')">
                            <v-select :options="deviceClassifyData" plain/>
                          </b-form-group>
                        </b-col>
                        <b-col></b-col>
                      </b-row>

                    </div>
                    <div class="align-self-center">
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('system-setting.search') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="info">{{ $t('system-setting.reset') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="success">{{ $t('system-setting.new') }}</b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.export') }}
                      </b-button>
                      <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.print') }}
                      </b-button>
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
                      <div slot="operating" slot-scope="props">
                        <b-button v-if="props.rowData.status === 'active'" size="xs" variant="info" disabled>
                          {{$t('system-setting.modify')}}
                        </b-button>
                        <b-button v-if="props.rowData.status === 'active'" size="xs" variant="primary">
                          {{$t('system-setting.status-inactive')}}
                        </b-button>
                        <b-button v-if="props.rowData.status === 'active'" size="xs" variant="danger" disabled>
                          {{$t('system-setting.delete')}}
                        </b-button>

                        <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="info">
                          {{$t('system-setting.modify')}}
                        </b-button>
                        <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="success">
                          {{$t('system-setting.status-active')}}
                        </b-button>
                        <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="danger">
                          {{$t('system-setting.delete')}}
                        </b-button>
                      </div>
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

      <b-tab :title="$t('device-management.business-stat')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4" no-body>
              <b-card-body>
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
  import 'vue-select/dist/vue-select.css'

  export default {
    components: {
      'input-tag': InputTag,
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        deviceClassifyData: [
          '全部',
          '监管查验设备',
          '单兵设备',
        ],
        selectedStatus: 'all',
        vuetableItems: {
          perPage: 5,
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'file-no',
              sortField: 'device-no',
              title: this.$t('device-management.file-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'filename',
              sortField: 'filename',
              title: this.$t('device-management.file-name'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'setting',
              sortField: 'setting',
              title: this.$t('device-management.setting'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('device-management.active'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturer',
              sortField: 'manufacturer',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'origin-no',
              sortField: 'origin-no',
              title: this.$t('device-management.origin-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "file-no": "0000",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "file-no": "0001",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "file-no": "0002",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "file-no": "0004",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 5,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "filename": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 6,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "filename": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 7,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "filename": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
        ],
        stateOptions: [
          {value: "all", label: this.$t('system-setting.status-all')},
          {value: "valid", label: this.$t('system-setting.status-active')},
          {value: "invalid", label: this.$t('system-setting.status-inactive')},
        ]
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
      }
    }
  }
</script>

