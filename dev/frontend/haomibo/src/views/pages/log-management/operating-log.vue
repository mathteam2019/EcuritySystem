  <style lang="scss">
    .col-form-label {
      margin-bottom: 1px;
    }

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
      <b-tab :title="$t('log-management.operating-log.access-log')" @click="tabStatus = 'access'">
        <b-row v-if="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="9">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-user')">
                      <b-form-input v-model="accessFilter.userName"/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating')">
                      <b-form-select v-model="accessFilter.action" :options="actionSelectData" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating-result')">
                      <b-form-select v-model="accessFilter.operateResult" :options="statusSelect" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="accessFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="accessFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
              <b-col cols="3" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAccessSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAccessResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()"
                            :disabled="checkPermItem('access_log_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintAccessButton()"
                            :disabled="checkPermItem('access_log_print')">
                    <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
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
                    @vuetable:checkbox-toggled="onCheckStatusChange"
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
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('log-management.operating-log.operating-log')" @click="tabStatus = 'operating'">
        <b-row class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-user')">
                      <b-form-input v-model="operatingFilter.userName"/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating')">
                      <b-form-select v-model="operatingFilter.action" :options="actionSelectDatas" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.object')">
                      <b-form-input v-model="operatingFilter.operateObject"/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.operating-result')">
                      <b-form-select v-model="operatingFilter.operateResult" :options="statusSelectData" plain/>
                    </b-form-group>
                  </b-col>

<!--                  <b-col>-->
<!--                    <b-form-group :label="$t('log-management.operating-log.client-ip')">-->
<!--                      <b-form-input v-model="operatingFilter.clientIp"/>-->
<!--                    </b-form-group>-->
<!--                  </b-col>-->

                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="operatingFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="operatingFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col/>
                  <b-col/>
                  <b-col/>
                  <b-col/>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onOperatingSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onOperatingResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()"
                            :disabled="checkPermItem('audit_log_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintOperatingButton()"
                            :disabled="checkPermItem('audit_log_print')">
                    <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
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
                    @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
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
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>
    <b-modal centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export')
            }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExportButtonModel()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
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
  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  import {validationMixin} from 'vuelidate';

  const {required} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'date-picker': DatePicker,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },
    mounted() {
      //this.handleWindowResize();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
      this.$refs.operatingLogTable.$parent.transform = this.transformOperatingTable.bind(this);
    },
    data() {
      return {
        isExpanded: false,
        pageStatus: 'table',
        showLength:20,
        direction: getDirection().direction,
        tabStatus: 'access',
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        renderedCheckList: [],
        renderedCheckListGroup: [],
        isModalVisible: false,
        accessFilter: {
          clientIp: null,
          action :null,
          operateResult :null,
          userName: null,
          operateStartTime: null,
          operateEndTime: null
        },
        operatingFilter: {
          userName: null,
          action: null,
          operateResult: null,
          operateObject: "",
          operateStartTime: null,
          operateEndTime: null

        },
        statusSelectData: [
          {value: null, text: this.$t('log-management.operating-log.status-all')},
          {value: '1', text: this.$t('log-management.operating-log.status-success')},
          {value: '0', text: this.$t('log-management.operating-log.status-failure')},
        ],
        statusSelect: [
          {value: null, text: this.$t('log-management.operating-log.status-all')},
          {value: '成功', text: '成功'},
          {value: '失败', text: '失败'},
        ],
        actionSelectData: [
          {value: null, text: this.$t('log-management.operating-log.status-all')},
          {value: '登录', text: '登录'},
          {value: '登出', text: '登出'},
        ],
        actionSelectDatas: [
          {value: null, text: this.$t('log-management.operating-log.status-all')},
          {value: '创建', text: '创建'},
          {value: '修改', text: '修改'},
          {value: '删除', text: '删除'},
          {value: '更新状态', text: '更新状态'},
          {value: '修改密码', text: '修改密码'},
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
              name: '__sequence',
              title: this.$t('log-management.operating-log.number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'operateAccount',
              title: this.$t('permission-management.user-account'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'user',
              title: this.$t('log-management.operating-log.access-user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (user) => {
                if (user == null) return '';
                return user.userName;
              }
            },
            {
              name: 'clientIp',
              title: this.$t('log-management.operating-log.access-ip'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'action',
              title: this.$t('log-management.operating-log.action'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'operateResult',
              title: this.$t('log-management.operating-log.operating-result'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'reasonCode',
              title: this.$t('log-management.operating-log.reason-code'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateTimeFormat',
              title: this.$t('log-management.operating-log.access-time'),
              sortField: 'operateTime',
              titleClass: 'text-center',
              dataClass: 'text-center',
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
              name: '__sequence',
              title: this.$t('log-management.operating-log.number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateAccount',
              title: this.$t('permission-management.user-account'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'user',
              title: this.$t('log-management.operating-log.access-user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (user) => {
                if (user == null) return '';
                return user.userName;
              }
            },
            {
              name: 'clientIp',
              title: this.$t('log-management.operating-log.client-ip'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'action',
              title: this.$t('log-management.operating-log.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateObject',
              title: this.$t('log-management.operating-log.object'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'operateResult',
              title: this.$t('log-management.operating-log.operating-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  '1': `<span class="text-success">${this.$t('log-management.operating-log.status-success')}</span>`,
                  '0': `<span class="text-muted">${this.$t('log-management.operating-log.status-failure')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'reasonCode',
              title: this.$t('log-management.operating-log.reason-code'),
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
      selectAll(value) {
        this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.vuetable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus() {
        let selectList = this.$refs.vuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNone();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAll(true);
            }
          }
        } else {
          this.selectNone();
        }

      },
      selectAllGroup(value) {
        this.$refs.operatingLogTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.operatingLogTable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.operatingLogTable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.operatingLogTable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup() {
        let selectList = this.$refs.operatingLogTable.selectedTo;
        let renderedList = this.renderedCheckListGroup;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNoneGroup();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAllGroup(true);
            }
          }
        } else {
          this.selectNoneGroup();
        }

      },
      onCheckStatusChange(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatus();
        } else {
          this.selectNone();
        }
      },
      onCheckStatusChangeGroup(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatusGroup();
        } else {
          this.selectNoneGroup();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        if (this.tabStatus === 'access') {
          this.onExportAccess();
        }
        if (this.tabStatus === 'operating') {
          this.onExportOperating();
        }
        this.isModalVisible = true;
      },
      onExportButtonModel() {
        if (this.tabStatus === 'access') {
          this.onExportAccess();
        }
        if (this.tabStatus === 'operating') {
          this.onExportOperating();
        }

      },
      onExportAccess() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.accessFilter,
          'idList': checkedIds.join()
        };
        this.link = `log-management/operating-log/access`;
        this.name = this.$t('log-management.operating-log.access-log');
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'Access-Log', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintAccessButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.accessFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/access`;
        printFileFromServer(link, params);
      },
      onExportOperating() {
        let checkedAll = this.$refs.operatingLogTable.checkedAllStatus;
        let checkedIds = this.$refs.operatingLogTable.selectedTo;
        let httpOption = this.$refs.operatingLogTable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.operatingFilter,
          'idList': checkedIds.join()
        };
        this.link = `log-management/operating-log/audit`;
        this.name = this.$t('log-management.operating-log.operating-log');
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'Operating-Log', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintOperatingButton() {
        let checkedAll = this.$refs.operatingLogTable.checkedAllStatus;
        let checkedIds = this.$refs.operatingLogTable.selectedTo;
        let httpOption = this.$refs.operatingLogTable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.operatingFilter,
          'idList': checkedIds.join()
        };
        let link = `log-management/operating-log/audit`;
        printFileFromServer(link, params);
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      onAccessSearchButton() {
        if(this.accessFilter.operateStartTime !== null && this.accessFilter.operateEndTime !== null) {

          if (this.accessFilter.operateStartTime >= this.accessFilter.operateEndTime) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`maintenance-management.process-task.time-select`), {
              duration: 3000,
              permanent: false
            });
            return;
          }

        }
        this.$refs.vuetable.refresh();
      },
      onAccessResetButton() {
        this.accessFilter = {
          clientIp: null,
          action :null,
          operateResult :null,
          userName: null,
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
          temp.operateTimeFormat = getDateTimeWithFormat(temp.operateTime, this.$i18n.locale);
          transformed.data.push(temp);
          this.renderedCheckList.push(data.data[i].id);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckList = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.accessFilter
        });
      },
      onvueTablePaginationData(paginationData) {
        this.$refs.vuetablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onvueTableChangePage(page) {
        this.$refs.vuetable.changePage(page);
        this.changeCheckAllStatus();
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
          temp.operateTimeFormat = getDateTimeWithFormat(temp.operateTime);
          transformed.data.push(temp);
          this.renderedCheckListGroup.push(data.data[i].id);
        }
        return transformed
      },
      operatingTableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckListGroup = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.operatingLogTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.operatingFilter
        });
      },
      onOperatingSearchButton() {
        if(this.operatingFilter.operateStartTime !== null && this.operatingFilter.operateEndTime !== null) {

          if (this.operatingFilter.operateStartTime >= this.operatingFilter.operateEndTime) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`maintenance-management.process-task.time-select`), {
              duration: 3000,
              permanent: false
            });
            return;
          }

        }
        this.$refs.operatingLogTable.refresh();
      },
      onOperatingResetButton() {
        this.operatingFilter = {
          userName:null,
          action:null,
          operateResult: null,
          operateObject: "",
          operateStartTime: null,
          operateEndTime: null
        };
      },
      onOperatingLogTablePaginationData(paginationData) {
        this.$refs.operatingLogPagination.setPaginationData(paginationData);
        this.changeCheckAllStatusGroup();
      },
      onOperatingLogTableChangePage(page) {
        this.$refs.operatingLogTable.changePage(page);
        this.changeCheckAllStatus();
      },
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.changeCheckAllStatus();
      },
      'operatingLogTableItems.perPage': function (newVal) {
        this.$refs.operatingLogTable.refresh();
        this.changeCheckAllStatusGroup();
      },

    }

  }
</script>
