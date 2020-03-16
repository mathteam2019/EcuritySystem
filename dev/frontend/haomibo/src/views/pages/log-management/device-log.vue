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
        <b-row v-if="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="deviceFilter.deviceName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="deviceFilter.userName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="deviceFilter.category"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="deviceFilter.level"/>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.device = !isExpanded.device">
                        <i :class="!isExpanded.device?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.device">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="deviceFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="deviceFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col/>
                  <b-col/>
                  <b-col/>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onDeviceSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onDeviceResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()" :disabled="checkPermItem('device_log_print')">
                    <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
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
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onSecurityLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="securityLogPagination"
                    @vuetable-pagination:change-page="onsecurityLogTableChangePage"
                    :initial-per-page="securityLogTableItems.perPage"
                    @onUpdatePerPage="securityLogTableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>


          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.device-log.decision-log')">
        <b-row v-if="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="judgeFilter.deviceName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.user')">
                      <b-form-input v-model="judgeFilter.userName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="judgeFilter.category"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="judgeFilter.level"/>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.judge = !isExpanded.judge">
                        <i :class="!isExpanded.judge?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.judge">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="judgeFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="judgeFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   />
                    </b-form-group>
                  </b-col>
                  <b-col/>
                  <b-col/>
                  <b-col/>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onJudgeSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onJudgeResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton('judge')" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton('judge')" :disabled="checkPermItem('device_log_print')">
                    <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
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
                    @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
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
                  />
                </div>
              </b-col>
            </b-row>


          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.device-log.hand-check-log')">
        <b-row v-if="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.access-user')">
                      <b-form-input v-model="manualFilter.operateAccount"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="manualFilter.deviceName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.category')">
                      <b-form-input v-model="manualFilter.category"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.level')">
                      <b-form-input v-model="manualFilter.level"/>
                    </b-form-group>
                  </b-col>
                  <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light"
                            @click="isExpanded.manual = !isExpanded.manual">
                        <i :class="!isExpanded.manual?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="8" v-if="isExpanded.manual">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.start-time')">
                      <date-picker v-model="manualFilter.operateStartTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('log-management.operating-log.end-time')">
                      <date-picker v-model="manualFilter.operateEndTime" type="datetime" format="MM/DD/YYYY HH:mm"
                                   placeholder=""/>
                    </b-form-group>
                  </b-col>
                  <b-col/>
                  <b-col/>
                  <b-col/>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onManualSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onManualResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton('manual')" :disabled="checkPermItem('device_log_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton('manual')" :disabled="checkPermItem('device_log_print')">
                    <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
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
                    @vuetable:checkbox-toggled="onCheckStatusChangeHand"
                    @vuetable:pagination-data="onhandCheckLogTablePaginationData"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="handCheckLogPagination"
                    @vuetable-pagination:change-page="onhandCheckLogTableChangePage"
                    :initial-per-page="handCheckLogTableItems.perPage"
                    @onUpdatePerPage="handCheckLogTableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>


          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>
    <b-modal  centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export') }}</h3>
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
        <b-button size="sm" variant="orange default" @click="onExportButtonModel(tabStatus)">
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
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {checkPermissionItem, getDirection} from "../../../utils";
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
    validations : {
      fileSelection : {
        required
      },
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
        direction: getDirection().direction,
        tabStatus : 'device',
        link: '',
        params: {},
        name: '',
        fileSelection : [],
        renderedCheckList:[],
        renderedCheckListGroup:[],
        renderedCheckListHand:[],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
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
              name: '__sequence',
              title: this.$t('log-management.device-log.number'),
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
              sortField : 'deviceSerial',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'category',
              title: this.$t('log-management.device-log.category'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'level',
              title: this.$t('log-management.device-log.level'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'content',
              title: this.$t('log-management.device-log.content'),
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
              name: '__sequence',
              title: this.$t('log-management.device-log.number'),
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
              sortField : 'deviceSerial',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'category',
              title: this.$t('log-management.device-log.category'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'level',
              title: this.$t('log-management.device-log.level'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'content',
              title: this.$t('log-management.device-log.content'),
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
              name: '__sequence',
              title: this.$t('log-management.device-log.number'),
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
              sortField : 'deviceSerial',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'loginName',
              title: this.$t('log-management.device-log.user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'category',
              title: this.$t('log-management.device-log.category'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'level',
              title: this.$t('log-management.device-log.level'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'content',
              title: this.$t('log-management.device-log.content'),
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
      selectAll(value){
        this.$refs.securityLogTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.securityLogTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.securityLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.securityLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.securityLogTable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNone();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAll(true);
            }
          }
        }
        else {
          this.selectNone();
        }

      },
      selectAllGroup(value){
        this.$refs.decisionLogTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.decisionLogTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.decisionLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.decisionLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup(){
        let selectList = this.$refs.decisionLogTable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNoneGroup();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAllGroup(true);
            }
          }
        }
        else {
          this.selectNoneGroup();
        }

      },
      selectAllHand(value){
        this.$refs.handCheckLogTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.handCheckLogTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.handCheckLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneHand(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.handCheckLogTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusHand(){
        let selectList = this.$refs.handCheckLogTable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNoneHand();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAllHand(true);
            }
          }
        }
        else {
          this.selectNone();
        }

      },
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
      },
      onCheckStatusChangeGroup(isChecked){
        if(isChecked){
          this.changeCheckAllStatusGroup();
        }
        else {
          this.selectNoneGroup();
        }
      },
      onCheckStatusChangeHand(isChecked){
        if(isChecked){
          this.changeCheckAllStatusHand();
        }
        else {
          this.selectNoneHand();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton(page = 'device') {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        this.tabStatus = page;
        this.onExportButtonModel(this.tabStatus);
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      onExportButtonModel(page) {
        let vueField = page === 'device' ? 'securityLogTable' : page === 'judge' ? 'decisionLogTable' : 'handCheckLogTable';
        let filter = page === 'device' ? this.deviceFilter : page === 'judge' ? this.judgeFilter : this.manualFilter;
        let checkedAll = this.$refs[vueField].checkedAllStatus;
        let checkedIds = this.$refs[vueField].selectedTo;
        let httpOption = this.$refs[vueField].httpOptions;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': filter,
          'idList': checkedIds.join()
        };
        this.link = `log-management/device-log`;
        this.name = 'device-log';
        this.isModalVisible = true;
        // downLoadFileFromServer(link, params, 'device-log');
      },
      onPrintButton(page = 'device') {
        let vueField = page === 'device' ? 'securityLogTable' : page === 'judge' ? 'decisionLogTable' : 'handCheckLogTable';
        let filter = page === 'device' ? this.deviceFilter : page === 'judge' ? this.judgeFilter : this.manualFilter;
        let checkedAll = this.$refs[vueField].checkedAllStatus;
        let checkedIds = this.$refs[vueField].selectedTo;
        let httpOption = this.$refs[vueField].httpOptions;
        console.log(httpOption);
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': filter,
          'idList': checkedIds.join()
        };
        let link = `log-management/device-log`;
        printFileFromServer(link, params);
      },

      transformTable(response) {
        let transformed = {};
        this.renderedCheckList =[];
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
          this.renderedCheckList.push(data.data[i].id);
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
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.securityLogTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.deviceFilter
        });
      },
      onSecurityLogTablePaginationData(paginationData) {
        this.$refs.securityLogPagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onsecurityLogTableChangePage(page) {
        this.$refs.securityLogTable.changePage(page);
        this.changeCheckAllStatus();
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
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.decisionLogTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.judgeFilter
        });
      },
      ondecisionLogTablePaginationData(paginationData) {
        this.$refs.decisionLogPagination.setPaginationData(paginationData);
        this.changeCheckAllStatusGroup();
      },
      ondecisionLogTableChangePage(page) {
        this.$refs.decisionLogTable.changePage(page);
        this.changeCheckAllStatusGroup();
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
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.handCheckLogTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.manualFilter
        });
      },
      onhandCheckLogTablePaginationData(paginationData) {
        this.$refs.handCheckLogPagination.setPaginationData(paginationData);
        this.changeCheckAllStatusHand();
      },
      onhandCheckLogTableChangePage(page) {
        this.$refs.handCheckLogTable.changePage(page);
        this.changeCheckAllStatusHand();
      },
    },
    watch: {
      'securityLogTableItems.perPage': function (newVal) {
        this.$refs.securityLogTable.refresh();
        this.changeCheckAllStatus();
      },
      'decisionLogTableItems.perPage': function (newVal) {
        this.$refs.decisionLogTable.refresh();
        this.changeCheckAllStatusGroup();
      },
      'handCheckLogTableItems.perPage': function (newVal) {
        this.$refs.handCheckLogTable.refresh();
        this.changeCheckAllStatusHand();
      },
    }
  }
</script>
