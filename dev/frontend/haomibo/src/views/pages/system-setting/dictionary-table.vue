<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-card if="!isLoading" class="main-without-tab">
      <b-row v-show="pageStatus==='table'" class="h-100">
        <b-col cols="9" class="d-flex flex-column">
          <div class="section d-flex flex-column h-100">
            <b-row class="m-0">
              <b-col cols="2" class="pr-3">
                <b-form-group>
                  <template slot="label">{{$t('system-setting.dictionary-name') }}</template>
                  <b-form-input v-model="dicName"/>
                </b-form-group>
              </b-col>
              <b-col cols="10" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="searchDic()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="resetDicSearchForm()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onClickCreateDicId()"
                            :disabled="checkPermItem('dictionary_create')" variant="success default">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 m-0">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <div v-show="loadingTable" class="overlay flex flex-column items-center justify-center">
                    <div class="loading_table"></div>
                  </div>
                  <vuetable
                    ref="vuetableId"
                    :fields="vuetableIdItems.fields"
                    :api-url="vuetableIdItems.apiUrl"
                    :http-fetch="vuetableIdHttpFetch"
                    :per-page="vuetableIdItems.perPage"
                    pagination-path="data"
                    data-path="data.data"
                    class="table-hover"
                    @vuetable:pagination-data="onDicPaginationData"
                    @vuetable:loading="loadingTable = true"
                    @vuetable:loaded="loadingTable = false"
                  >
                    <template slot="dictionaryName" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onDetailClicked(props.rowData)">
                        {{props.rowData.dictionaryName}}
                      </span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div v-if="props.rowData.dictionaryType === 2">
                        <b-button
                          size="sm" :disabled="checkPermItem('dictionary_modify')"
                          variant="primary default btn-square" @click="onDicModifyClicked(props.rowData)">
                          <i class="icofont-edit"/>
                        </b-button>
                        <b-button size="sm" variant="danger default btn-square" class="m-0"
                                  :disabled="checkPermItem('dictionary_delete')"
                                  @click="onClickDeleteDicId(props.rowData)">
                          <i class="icofont-bin"/>
                        </b-button>
                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="dicPagination"
                    @vuetable-pagination:change-page="onDicChangePage"
                    :initial-per-page="vuetableIdItems.perPage"
                    @onUpdatePerPage="vuetableIdItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </div>
        </b-col>
        <b-col cols="3" class="pl-0" v-if="selectedDic || dicForm.visible">
          <div class="section d-flex flex-column h-100 px-3">
            <div v-if="selectedDic || dicForm.visible" style="margin-left: 1.5rem; margin-right: 1.5rem;"
                 class="flex-grow-1">
              <b-form class="h-100 d-flex flex-column">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.dictionary-name') }}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dicForm.dicName"
                    :state="!$v.dicForm.dicName.$invalid"
                    />
                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.th-org-note') }}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-textarea
                    v-model="dicForm.note" rows="4"
                    :state="!$v.dicForm.note.$invalid"
                    :placeholder="$t('system-setting.please-enter-remarks')"
                  />
                </b-form-group>

                <div class="d-flex align-items-end justify-content-end pt-3">
                  <div>
                    <b-button @click="onClickSaveDic" size="sm" variant="info default" class="mr-3">
                      <i class="icofont-save"/>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>
                  </div>
                </div>

              </b-form>
            </div>
          </div>
        </b-col>
      </b-row>
      <b-row v-show="pageStatus==='modify'" class="h-100">
        <b-col cols="9" class="d-flex flex-column">
          <div class="section d-flex flex-column h-100">
            <b-row class="m-0">
              <b-col cols="2" class="pr-3">
                <b-form-group>
                  <template slot="label"> {{$t('system-setting.dictionary-number') }}</template>
                  <b-form-input v-model="dicDataCode"/>
                </b-form-group>
              </b-col>
              <b-col cols="2" class="pr-3">
                <b-form-group>
                  <template slot="label"> {{$t('system-setting.dictionary-value') }}</template>
                  <b-form-input v-model="dicDataValue"/>
                </b-form-group>
              </b-col>
              <b-col cols="8" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="searchDicData()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="resetDicDataSearchForm()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" variant="info default" @click="pageStatus='table'">
                    <i class="icofont-long-arrow-left"/>
                    {{ $t('personal-inspection.return') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onClickCreateDicData()"
                            :disabled="checkPermItem('dictionary_data_create') || dicIdType === 1" variant="success default">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 m-0">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <div v-show="loadingTable" class="overlay flex flex-column items-center justify-center">
                    <div class="loading_table"></div>
                  </div>
                  <vuetable
                    ref="vuetableData"
                    :fields="vuetableDataItems.fields"
                    :api-url="vuetableDataItems.apiUrl"
                    :http-fetch="vuetableDataHttpFetch"
                    :per-page="vuetableDataItems.perPage"
                    pagination-path="data"
                    data-path="data.data"
                    class="table-hover"
                    @vuetable:pagination-data="onDicDataPaginationData"
                    @vuetable:loading="loadingTable = true"
                    @vuetable:loaded="loadingTable = false"
                  >
                    <template slot="dataCode" slot-scope="props">
                      <span v-if="checkPermItem('dictionary_data_modify')" class="cursor-p text-primary">
                        {{props.rowData.dataCode}}
                      </span>
                      <span v-else class="cursor-p text-primary" @click="onDicDataRowClicked(props.rowData)">
                        {{props.rowData.dataCode}}
                      </span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div v-if="dicIdType === 2">
                      <b-button
                        size="sm" :disabled="checkPermItem('dictionary_data_modify')"
                        variant="primary default btn-square" @click="onDicDataModifyClicked(props.rowData)">
                        <i class="icofont-edit"/>
                      </b-button>
                      <b-button size="sm" variant="danger default btn-square" class="m-0"
                                :disabled="checkPermItem('dictionary_data_delete')"
                                @click="onClickDeleteDicData(props.rowData)">
                        <i class="icofont-bin"/>
                      </b-button>
                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="dicDataPagination"
                    @vuetable-pagination:change-page="onDicDataChangePage"
                    :initial-per-page="vuetableDataItems.perPage"
                    @onUpdatePerPage="vuetableDataItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </div>
        </b-col>
        <b-col cols="3" class="pl-0" v-if="selectedDicData || dicDataForm.visible">
          <div class="section d-flex flex-column h-100 px-3">
            <div v-if="selectedDicData || dicDataForm.visible" style="margin-left: 1.5rem; margin-right: 1.5rem;"
                 class="flex-grow-1">
              <b-form class="h-100 d-flex flex-column">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.dictionary-number') }}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dicDataForm.dicDataCode"
                    :state="!$v.dicDataForm.dicDataCode.$invalid"
                    :placeholder="$t('system-setting.dictionary-number-input')"/>
                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.dictionary-value') }}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dicDataForm.dicDataValue"
                    :state="!$v.dicDataForm.dicDataValue.$invalid"
                    :placeholder="$t('system-setting.dictionary-value-input')"/>
                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.th-org-note') }}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-textarea
                    v-model="dicDataForm.note" rows="4"
                    :state="!$v.dicDataForm.note.$invalid"
                    :placeholder="$t('system-setting.please-enter-remarks')"
                  />
                </b-form-group>

                <div class="d-flex align-items-end justify-content-end pt-3">
                  <div>
                    <b-button @click="onClickSaveDicData" :disabled="dicIdType === 1 || rowClicked" size="sm" variant="info default" class="mr-3">
                      <i class="icofont-save"/>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>
                  </div>
                </div>
              </b-form>
            </div>
          </div>
        </b-col>
      </b-row>
    </b-card>
    <div v-if="isLoading" class="loading"></div>
    <b-modal centered id="modal-delete-dic" ref="modal-delete-dic"
             :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.delete-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" class="mr-1" @click="deleteDic">{{$t('system-setting.ok')}}</b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-dic')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered id="modal-delete-dicData" ref="modal-delete-dicData"
             :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.delete-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" class="mr-1" @click="deleteDicData">{{$t('system-setting.ok')}}</b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-dicData')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<style>

  .col-form-label {
    margin-bottom: 1px;
    /*border-radius: 0.3125rem;*/
    /*border-top-left-radius: 0.3125rem;*/
    /*border-top-right-radius: 0.3125rem;*/
    /*border-bottom-right-radius: 0.3125rem;*/
    /*border-bottom-left-radius: 0.3125rem;*/
    /*padding: 1.5rem 0 1rem 0;*/
    /*border: solid 1px #cccccc;*/
  }

  .halo-tree .inputCheck {
    top: 2px !important;
  }

  .tb-icon {
    font-size: 20px;
  }

  .tb-button {
    font-size: 20px;
    cursor: pointer;
  }

  .tb-button-disabled {
    font-size: 20px;
    color: lightgray !important;
  }

  span.cursor-p {
    cursor: pointer !important;
  }

  .h-35vh {
    height: 32vh;
    max-height: 33vh;
    overflow: auto;
  }
</style>
<style lang="scss">
  .loading_table {
    display: inline-block;
    width: 30px;
    height: 30px;
    border: 2px solid rgba(#145388, 0.2);
    border-radius: 50%;
    border-top-color: #145388;
    animation: spin 1s ease-in-out infinite;
    -webkit-animation: spin 1s ease-in-out infinite;
    left: calc(38% - 15px);
    top: calc(50% - 15px);
    position: fixed;
    z-index: 1;
  }
  .search-form-group {
    [dic="group"] {
      position: relative;

      .form-control {
        padding-right: 30px;
      }

      .search-input-icon {
        position: absolute;
        top: 50%;
        right: 1em;
        transform: translateY(-50%);
      }

    }

  }
</style>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import axios from 'axios'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import {checkPermissionItem, getDirection, savePermissionInfo, getLocale} from "../../../utils";
  import _ from "lodash";
  import {validationMixin} from 'vuelidate';

  const {required, minLength, maxLength} = require('vuelidate/lib/validators');
  import {responseMessages} from '../../../constants/response-messages';

  import staticUserTableData from '../../../data/user'
  import {
    downLoadFileFromServer,
    getApiManager,
    isDataCodeValid,
    getDateTimeWithFormat,
    printFileFromServer
  } from "../../../api";

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
    },
    mounted() {
      //this.$refs.vuetableData.$parent.transform = this.fnTransformDataTable.bind(this);
    },
    mixins: [validationMixin],
    data() {
      return {
        pageStatus: 'table',
        pageStatus1: '',
        rowClicked:false,
        isLoading: false,
        loadingTable:false,
        dicForm: {
          visible: false,
          dicName: '',
          dicGrade: '',
          note: ''
        },
        dicDataForm: {
          visible: false,
          dicDataCode: '',
          dicDataValue: '',
          note: ''
        },

        dicName: '',
        dicDataId: null,
        dicDataCode: null,
        dicDataValue: null,
        resourceList: [],
        resourceTreeData: [],
        selectedDic: null,
        selectedDicData: null,
        deletingDic: null,
        deletingDicData: null,
        dicIdType:null,

        vuetableIdItems: {
          apiUrl: `${apiBaseUrl}/dictionary-management/dictionary/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.permission-control.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:dictionaryName',
              title: this.$t('system-setting.dictionary-name'),
              sortField: 'dictionaryName',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '19%'
            },
            {
              name: 'note',
              title: this.$t('permission-management.th-org-note'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '19%',
            },
            {
              name: 'createdTime',
              title: this.$t('system-setting.time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%',
              callback: (createdTime) => {
                if (createdTime == null) return '';
                return getDateTimeWithFormat(createdTime);
              }
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ]
        },
        vuetableDataItems: {
          apiUrl: `${apiBaseUrl}/dictionary-management/dictionary-data/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.permission-control.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: '__slot:dataCode',
              title: this.$t('system-setting.dictionary-number'),
              sortField: 'dataCode',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'dataValue',
              title: this.$t('system-setting.dictionary-value'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'note',
              title: this.$t('permission-management.th-org-note'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: 'createdTime',
              title: this.$t('system-setting.time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%',
              callback: (createdTime) => {
                if (createdTime == null) return '';
                return getDateTimeWithFormat(createdTime);
              }
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ]
        },
      }
    },
    validations: {
      dicForm: {
        dicName: {
          required
        },
        note:{
          required
        }
      },
      dicDataForm: {
        dicDataCode: {
          isDataCodeValid,
          required, minLength: minLength(10), maxLength: maxLength(10),
        },
        dicDataValue: {
          required
        },
        note: {
          required
        }
      },
    },
    watch: {
      'vuetableIdItems.perPage': function (newVal) {
        this.$refs.vuetableId.refresh();
        this.selectedDic = false;
        this.dicForm.visible = false;
      },

      'vuetableDataItems.perPage': function (newVal) {
        this.$refs.vuetableData.refresh();
        this.selectedDicData = false;
        this.dicDataForm.visible = false;
      },

    },
    methods: {

      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      getDataCodeValue(value) {
        const dictionary = {

          "1000001401": `枪支`,
          "1000001402": `药品`,
          "1000001403": `其他`,
          "1000001501": `1级`,
          "1000001502": `2级`,
          "1000001503": `3级`,
          "1000001504": `4级`,
          "1000001505": `5级`,
        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      hideModal(refName) {
        this.$refs[refName].hide();
      },

      searchDic() {
        this.$refs.vuetableId.refresh();
      },
      searchDicData() {
        this.$refs.vuetableData.refresh();
      },
      resetDicSearchForm() {
        this.dicName = null;
      },
      resetDicDataSearchForm() {
        this.dicDataCode = null;
        this.dicDataValue = null;
      },
      onClickCreateDicId() {
        this.selectedDic = null;
        this.dicForm = {
          visible: true,
          dicName: '',
          dicGrade: '',
          note: ''
        }
      },
      onClickCreateDicData() {
        this.selectedDicData = null;
        this.rowClicked = false;
        this.dicDataForm = {
          visible: true,
          dicDataCode: '',
          dicDataVaule: '',
          note: ''
        }
      },
      onClickSaveDic() {

        if (this.$v.dicForm.$invalid) {
          if(this.dicForm.dicName===""){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.dictionary-invalid`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.dicForm.note===""){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.please-enter-remarks`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }

        //this.isLoading = true;

        if (this.selectedDic) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary/modify`, {
              'dictionaryId': this.selectedDic.dictionaryId,
              'dictionaryName': this.dicForm.dicName,
              'note': this.dicForm.note,
            })
            .then((response) => {
              //this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-modify`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.vuetableId.reload();
                  break;
                case responseMessages['used-dictionary-value']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-children']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.dictionary-has-children`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-messages.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }
        if (this.dicForm.visible) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary/create`, {
              'dictionaryName': this.dicForm.dicName,
              'note': this.dicForm.note,
            })
            .then((response) => {
              //this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-create`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.vuetableId.reload();
                  break;
                case responseMessages['used-dictionary-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-messages.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }

      },
      onClickSaveDicData() {

        if (this.$v.dicDataForm.$invalid) {
          if (this.$v.dicDataForm.dicDataCode.$invalid) {
            if(this.$v.dicDataForm.dicDataCode==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.dictionary-number-input`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.dictionary-number-valid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
          }
          if (this.$v.dicDataForm.dicDataValue.$invalid) {

              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.dictionary-value-input`), {
                duration: 3000,
                permanent: false
              });
              return;

          }
          if(this.dicDataForm.note===""){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.please-enter-remarks`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        //this.isLoading = true;

        if (this.selectedDicData) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary-data/modify`, {
              'dataId': this.selectedDicData.dataId,
              'dictionaryId': this.dicDataId,
              'dataCode': this.dicDataForm.dicDataCode,
              'dataValue': this.dicDataForm.dicDataValue,
              'note': this.dicDataForm.note,
            })
            .then((response) => {
              //this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-modify`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.vuetableData.reload();
                  break;
                case responseMessages['used-dictionary-code']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-code`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-dictionary-value']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-value`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-messages.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }
        if (this.dicDataForm.visible) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary-data/create`, {
              'dictionaryId': this.dicDataId,
              'dataCode': this.dicDataForm.dicDataCode,
              'dataValue': this.dicDataForm.dicDataValue,
              'note': this.dicDataForm.note,
            })
            .then((response) => {
              //this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-create`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.vuetableData.reload();
                  break;
                case responseMessages['used-dictionary-code']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-code`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-dictionary-value']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-dictionary-value`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }

      },

      onClickDeleteDicId(dic) {
        this.deletingDic = dic;
        this.$refs['modal-delete-dic'].show();
      },
      onClickDeleteDicData(dicData) {
        this.deletingDicData = dicData;
        this.$refs['modal-delete-dicData'].show();
      },

      onDicModifyClicked(dataItem) {
        this.dicForm.visible = false;
        this.selectedDic = JSON.parse(JSON.stringify(dataItem));
        this.dicForm.dicName = this.selectedDic.dictionaryName;
        this.dicForm.note = this.selectedDic.note;
      },
      onDicDataRowClicked(dataItem) {
        this.rowClicked = true;
        this.selectedDicData = JSON.parse(JSON.stringify(dataItem));
        this.dicDataForm.dicDataCode = this.selectedDicData.dataCode;
        this.dicDataForm.dicDataValue = this.selectedDicData.dataValue;
        this.dicDataForm.note = this.selectedDicData.note;
      },
      onDicDataModifyClicked(dataItem) {
        this.dicDataForm.visible = false;
        this.rowClicked = false;
        this.selectedDicData = JSON.parse(JSON.stringify(dataItem));
        this.dicDataForm.dicDataCode = this.selectedDicData.dataCode;
        this.dicDataForm.dicDataValue = this.selectedDicData.dataValue;
        this.dicDataForm.note = this.selectedDicData.note;
      },

      onDetailClicked(dataItem) {
        this.dicDataId = dataItem.dictionaryId;
        this.dicIdType = dataItem.dictionaryType;
        this.pageStatus = 'modify';
        this.resetDicDataSearchForm();
        // this.pageStatus = 'table';
        this.$refs.vuetableData.refresh();
      },

      deleteDic() {
        if (this.deletingDic) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary/delete`, {
              'dictionaryId': this.deletingDic.dictionaryId
            })
            .then((response) => {
              //this.isLoading = false;
              this.hideModal('modal-delete-dic');
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-delete`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.deletingDic = null;
                  this.$refs.vuetableId.refresh();
                  break;
                case responseMessages['has-children']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-children`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }
      },
      deleteDicData() {
        if (this.deletingDicData) {
          getApiManager()
            .post(`${apiBaseUrl}/dictionary-management/dictionary-data/delete`, {
              'dataId': this.deletingDicData.dataId
            })
            .then((response) => {
              //this.isLoading = false;
              this.hideModal('modal-delete-dicData');
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.dictionary-delete`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.deletingDicData = null;
                  this.$refs.vuetableData.refresh();
                  break;
                case responseMessages['has-children']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-children`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:
              }
            })
            .catch((error) => {
              //this.isLoading = false;
            });
        }
      },

      vuetableIdHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableIdItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            dictionaryName: this.dicName,
          }
        });
      },
      vuetableDataHttpFetch(apiUrl, httpOptions) {
        if (this.dicDataId === null) return null;
        else {
          return getApiManager().post(apiUrl, {
            currentPage: httpOptions.params.page,
            perPage: this.vuetableDataItems.perPage,
            sort: httpOptions.params.sort,
            filter: {
              dataCode: this.dicDataCode,
              dataValue: this.dicDataValue,
              dictionaryId: this.dicDataId,
            }
          });
        }
      },

      onDicPaginationData(paginationData) {
        this.$refs.dicPagination.setPaginationData(paginationData);
        this.selectedDic = false;
        this.dicForm.visible = false;
      },
      onDicDataPaginationData(paginationData) {
        this.$refs.dicDataPagination.setPaginationData(paginationData);
        this.selectedDicData = false;
        this.dicDataForm.visible = false;
      },

      onDicChangePage(page) {
        this.$refs.vuetableId.changePage(page);
        this.selectedDic = false;
        this.dicForm.visible = false;
      },
      onDicDataChangePage(page) {
        this.$refs.vuetableData.changePage(page);
        this.selectedDicData = false;
        this.dicDataForm.visible = false;
      }
    }
  }
</script>
