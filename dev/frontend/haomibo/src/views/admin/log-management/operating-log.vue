<style lang="scss">

  .operating-log {
    .rounded-span {
      width: 20px;
      height: 20px;
      border-radius: 10px;
      cursor: pointer;
      background-color: #007bff;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

</style>
<template>
  <div class="operating-log">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('log-management.operating-log.access-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-ip')">
                      <b-form-input v-model="accessFilter.clientIp"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="accessFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm" valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="accessFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm" valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-user')">
                      <b-form-input v-model="accessFilter.operateAccount"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAccessSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAccessResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportAccessButton()" :disabled="checkPermItem('access_log_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintAccessButton()" :disabled="checkPermItem('access_log_print')">
                    <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :fields="vuetableItems.fields"
                    :api-url="vuetableItems.apiUrl"
                    :http-fetch="vuetableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    track-by="id"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onvueTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="vuetablePagination"
                    @vuetable-pagination:change-page="onvueTableChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
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
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="operatingFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm" valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="operatingFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm" valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.client-ip')">
                      <b-form-input v-model="operatingFilter.clientIp"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating-result')">
                      <b-form-select v-model="operatingFilter.operateResult" :options="statusSelectData" plain/>
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
                    <b-form-group :label="$t('log-management.operating-log.object')">
                      <b-form-input v-model="operatingFilter.operateObject"></b-form-input>
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
                  <b-button size="sm" class="ml-2" variant="info default" @click="onOperatingSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onOperatingResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportOperatingButton()" :disabled="checkPermItem('audit_log_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintOperatingButton()" :disabled="checkPermItem('audit_log_print')">
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
                    :fields="operatingLogTableItems.fields"
                    :api-url="operatingLogTableItems.apiUrl"
                    :http-fetch="operatingTableHttpFetch"
                    pagination-path="pagination"
                    track-by="id"
                    class="table-striped"
                    @vuetable:pagination-data="onOperatingLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="operatingLogPagination"
                    @vuetable-pagination:change-page="onOperatingLogTableChangePage"
                    :initial-per-page="operatingLogTableItems.perPage"
                    @onUpdatePerPage="operatingLogTableItems.perPage = Number($event)"
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
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {downLoadFileFromServer, getApiManager, getDateTimeWithFormat, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {checkPermissionItem} from "../../../utils";


  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'date-picker': DatePicker,
    },
    mounted() {
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
      this.$refs.operatingLogTable.$parent.transform = this.transformOperatingTable.bind(this);
    },
    data() {
      return {
        isExpanded: false,
        pageStatus: 'table',
        accessFilter: {
          clientIp: null,
          operateAccount: null,
          operateStartTime: null,
          operateEndTime: null
        },
        operatingFilter: {
          clientIp: "",
          operateResult: "",
          operateObject: "",
          operateStartTime: null,
          operateEndTime: null

        },
        statusSelectData: [
          {value: null, text: this.$t('log-management.operating-log.status-all')},
          {value: 'active', text: this.$t('log-management.operating-log.status-success')},
          {value: 'inactive', text: this.$t('log-management.operating-log.status-failure')},
        ],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/log-management/operating-log/access/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'operateId',
              title: this.$t('log-management.operating-log.number'),
              sortField: 'operateId',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'operateTimeFormat',
              title: this.$t('log-management.operating-log.access-time'),
              sortField: 'operateTime',
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
              name: 'clientIp',
              title: this.$t('log-management.operating-log.access-ip'),
              sortField: 'clientIp',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'operateAccount',
              title: this.$t('log-management.operating-log.access-user'),
              sortField: 'operateAccount',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ],
          perPage: 10,
        },
        //second tab content
        operatingLogTableItems: {
          apiUrl: `${apiBaseUrl}/log-management/operating-log/audit/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('log-management.operating-log.number'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operatorId',
              title: this.$t('log-management.operating-log.user-id'),
              sortField: 'operatorId',
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
              name: 'operateObject',
              title: this.$t('log-management.operating-log.object'),
              sortField: 'operateObject',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'action',
              title: this.$t('log-management.operating-log.operating'),
              sortField: 'action',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateContent',
              title: this.$t('log-management.operating-log.operating-content'),
              sortField: 'operateContent',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateResult',
              title: this.$t('log-management.operating-log.operating-result'),
              sortField: 'operateResult',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'reasonCode',
              title: this.$t('log-management.operating-log.operating-failure-code'),
              sortField: 'reasonCode',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateTimeFormat',
              title: this.$t('log-management.operating-log.operating-time'),
              sortField: 'operateTime',
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
        },
      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportAccessButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.accessFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/access`;
        downLoadFileFromServer(link, params, 'access-log');
      },
      onPrintAccessButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.accessFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/access`;
        printFileFromServer(link, params);
      },
      onExportOperatingButton() {
        let checkedAll = this.$refs.operatingLogTable.checkedAllStatus;
        let checkedIds = this.$refs.operatingLogTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.operatingFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/audit`;
        downLoadFileFromServer(link, params, 'operating-log');
      },
      onPrintOperatingButton() {
        let checkedAll = this.$refs.operatingLogTable.checkedAllStatus;
        let checkedIds = this.$refs.operatingLogTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.operatingFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/audit`;
        printFileFromServer(link, params);
      },
      onAccessSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onAccessResetButton() {
        this.accessFilter = {
          clientIp: null,
          operateAccount: null,
          operateStartTime: null,
          operateEndTime: null
        };
      },
      transformTable(response) {
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
          temp.operateTimeFormat  = getDateTimeWithFormat(temp.operateTime,this.$i18n.locale);
          transformed.data.push(temp);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.accessFilter
        });
      },
      onvueTablePaginationData(paginationData) {
        this.$refs.vuetablePagination.setPaginationData(paginationData);
      },
      onvueTableChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },

      transformOperatingTable(response) {
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
          temp.operateTimeFormat  = getDateTimeWithFormat(temp.operateTime);
          transformed.data.push(temp);
        }
        return transformed
      },
      operatingTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.operatingLogTableItems.perPage,
          filter: this.operatingFilter
        });
      },
      onOperatingSearchButton() {
        this.$refs.operatingLogTable.refresh();
      },
      onOperatingResetButton() {
        this.operatingFilter = {
          clientIp: "",
          operateResult: "",
          operateObject: "",
          operateStartTime: null,
          operateEndTime: null
        };
      },
      onOperatingLogTablePaginationData(paginationData) {
        this.$refs.operatingLogPagination.setPaginationData(paginationData);
      },
      onOperatingLogTableChangePage(page) {
        this.$refs.operatingLogTable.changePage(page);
      },
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      'operatingLogTableItems.perPage': function (newVal) {
        this.$refs.operatingLogTable.refresh();
      },

    }

  }
</script>
