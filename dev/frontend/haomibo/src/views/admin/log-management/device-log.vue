<style lang="scss">

  .device-log {
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
  <div class="device-log">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>


    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('log-management.device-log.security-log')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="deviceFilter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="deviceFilter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="deviceFilter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="deviceFilter.level"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.device = !isExpanded.device">
                        <i :class="!isExpanded.device?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.device">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="deviceFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="deviceFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onDeviceSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onDeviceResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()" :disabled="checkPermItem('device_log_print')">
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
                    :api-url="securityLogTableItems.apiUrl"
                    :http-fetch="securityLogTableHttpFetch"
                    :fields="securityLogTableItems.fields"
                    :per-page="securityLogTableItems.perPage"
                    pagination-path="pagination"
                    track-by="id"
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
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
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
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="judgeFilter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="judgeFilter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="judgeFilter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="judgeFilter.level"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.judge = !isExpanded.judge">
                        <i :class="!isExpanded.judge?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.judge">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="judgeFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="judgeFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss"></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onJudgeSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onJudgeResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton('judge')" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton('judge')" :disabled="checkPermItem('device_log_print')">
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
                    :api-url="decisionLogTableItems.apiUrl"
                    :http-fetch="decisionLogTableHttpFetch"
                    :fields="decisionLogTableItems.fields"
                    :per-page="decisionLogTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    track-by="id"
                    @vuetable:pagination-data="ondecisionLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="decisionLogPagination"
                    @vuetable-pagination:change-page="ondecisionLogTableChangePage"
                    :initial-per-page="decisionLogTableItems.perPage"
                    @onUpdatePerPage="decisionLogTableItems.perPage = Number($event)"
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
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="manualFilter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="manualFilter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="manualFilter.category"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="manualFilter.level"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.manual = !isExpanded.manual">
                        <i :class="!isExpanded.manual?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.manual">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="manualFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="manualFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   valueType="YYYY-MM-DD HH:mm:ss" placeholder=""></date-picker>
                    </b-form-group>
                  </b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                  <b-col></b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onManualSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onManualResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton('manual')" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton('manual')" :disabled="checkPermItem('device_log_print')">
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
                    :api-url="handCheckLogTableItems.apiUrl"
                    :http-fetch="handCheckLogTableHttpFetch"
                    :fields="handCheckLogTableItems.fields"
                    :per-page="handCheckLogTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    track-by="id"
                    @onUpdatePerPage="handCheckLogTableItems.perPage = Number($event)"
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
      this.$refs.securityLogTable.$parent.transform = this.transformTable.bind(this);
      this.$refs.decisionLogTable.$parent.transform = this.transformTable.bind(this);
      this.$refs.handCheckLogTable.$parent.transform = this.transformTable.bind(this);
    },
    data() {
      return {
        isExpanded: {
          'device': false,
          'judge': false,
          'manual': false
        },
        pageStatus: 'table',
        deviceFilter: {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001901'
        },
        judgeFilter: {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001902'
        },
        manualFilter: {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001903'
        },
        //first tab
        securityLogTableItems: {
          apiUrl: `${apiBaseUrl}/log-management/device-log/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('log-management.device-log.number'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceName',
              title: this.$t('log-management.device-log.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceSerial',
              title: this.$t('log-management.device-log.device-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              sortField: 'loginName',
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
              name: 'operateTime',
              title: this.$t('log-management.device-log.operating-time'),
              sortField: 'time',
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
          perPage: 10,
        },
        //second tab
        decisionLogTableItems: {
          apiUrl: `${apiBaseUrl}/log-management/device-log/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('log-management.device-log.number'),
              sortField: 'number',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceName',
              title: this.$t('log-management.device-log.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceSerial',
              title: this.$t('log-management.device-log.device-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              sortField: 'loginName',
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
              name: 'operateTime',
              title: this.$t('log-management.device-log.operating-time'),
              sortField: 'time',
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
          perPage: 10,
        },
        //third tab
        handCheckLogTableItems: {
          apiUrl: `${apiBaseUrl}/log-management/device-log/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('log-management.device-log.number'),
              sortField: 'number',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceName',
              title: this.$t('log-management.device-log.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceSerial',
              title: this.$t('log-management.device-log.device-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              sortField: 'loginName',
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
              name: 'operateTime',
              title: this.$t('log-management.device-log.operating-time'),
              sortField: 'time',
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
          perPage: 10,
        },
      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton(page = 'device') {
        let vueField = page === 'device' ? 'securityLogTable' : page === 'judge' ? 'decisionLogTable' : 'handCheckLogTable';
        let filter = page === 'device' ? this.deviceFilter : page === 'judge' ? this.judgeFilter : this.manualFilter;
        let checkedAll = this.$refs[vueField].checkedAllStatus;
        let checkedIds = this.$refs[vueField].selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': filter,
          'idList': checkedIds.join()
        };
        let link = `log-management/device-log`;
        downLoadFileFromServer(link, params, 'device-log');
      },
      onPrintButton(page = 'device') {
        let vueField = page === 'device' ? 'securityLogTable' : page === 'judge' ? 'decisionLogTable' : 'handCheckLogTable';
        let filter = page === 'device' ? this.deviceFilter : page === 'judge' ? this.judgeFilter : this.manualFilter;
        let checkedAll = this.$refs[vueField].checkedAllStatus;
        let checkedIds = this.$refs[vueField].selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': filter,
          'idList': checkedIds.join()
        };
        let link = `log-management/device-log`;
        printFileFromServer(link, params);
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
          temp.deviceName = temp.device.deviceName;
          temp.operateTime  = getDateTimeWithFormat(temp.time,this.$i18n.locale);
          temp.deviceSerial = temp.device.deviceSerial;
          transformed.data.push(temp);
        }
        return transformed
      },
      onDeviceSearchButton() {
        this.$refs.securityLogTable.refresh();
      },
      onDeviceResetButton() {
        this.deviceFilter = {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001901'
        };
      },
      securityLogTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.securityLogTableItems.perPage,
          filter: this.deviceFilter
        });
      },
      onSecurityLogTablePaginationData(paginationData) {
        this.$refs.securityLogPagination.setPaginationData(paginationData);
      },
      onsecurityLogTableChangePage(page) {
        this.$refs.securityLogTable.changePage(page);
      },

      //second tab
      onJudgeSearchButton() {
        this.$refs.decisionLogTable.refresh();
      },
      onJudgeResetButton() {
        this.judgeFilter = {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001902'
        };
      },
      decisionLogTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.decisionLogTableItems.perPage,
          filter: this.judgeFilter
        });
      },
      ondecisionLogTablePaginationData(paginationData) {
        this.$refs.decisionLogPagination.setPaginationData(paginationData);
      },
      ondecisionLogTableChangePage(page) {
        this.$refs.decisionLogTable.changePage(page);
      },

      //third tab
      onManualSearchButton() {
        this.$refs.handCheckLogTable.refresh();
      },
      onManualResetButton() {
        this.manualFilter = {
          operateStartTime: null,
          operateEndTime: null,
          deviceName: '',
          userName: '',
          category: '',
          level: '',
          deviceType: '1000001903'
        };
      },
      handCheckLogTableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.handCheckLogTableItems.perPage,
          filter: this.manualFilter
        });
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
