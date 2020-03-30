<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card v-show="!isLoading" class="main-without-tab" nav-class="ml-2" :no-fade="true">
      <div v-show="pageStatus==='list'" class="h-100 flex-column" :class="pageStatus === 'list'?'d-flex':''">
        <b-row class="pt-2">
          <b-col cols="6">
            <b-row>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.template')">
                  <b-form-input v-model="filterOption.templateName"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('permission-management.status')">
                  <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="filterOption.categoryId" :options="categoryFilterData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()"
                      :disabled="checkPermItem('device_template_export')">
              <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton()"
                      :disabled="checkPermItem('device_template_print')">
              <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default"
                      :disabled="checkPermItem('device_template_create')">
              <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
            </b-button>
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
                pagination-path="pagination"
                track-by="archivesTemplateId"
                @vuetable:checkbox-toggled="onCheckStatusChange"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped text-center"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.archivesTemplateNumber }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit',props.rowData)"
                            size="sm"
                            :disabled="checkPermItem('device_template_modify')"
                            variant="primary default btn-square">
                    <i class="icofont-edit"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)"
                    :disabled="checkPermItem('device_template_update_status')"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000701'"
                    :disabled="checkPermItem('device_template_update_status')"
                    size="sm" @click="onAction('inactivate',props.rowData)"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"/>
                  </b-button>
                  <b-button @click="onAction('delete',props.rowData)"
                            size="sm"
                            :disabled="props.rowData.status === '1000000701' || checkPermItem('device_template_delete')"
                            variant="danger default btn-square">
                    <i class="icofont-bin"/>
                  </b-button>

                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
                :initial-per-page="vuetableItems.perPage"
                @onUpdatePerPage="vuetableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>
      </div>
      <div v-show="pageStatus !== 'list'" class="h-100">
        <div class="form-section d-flex flex-column">
          <b-row class="h-100">
            <b-col xxs="12" md="4" lg="3">
              <b-form-group>
                <template slot="label">{{$t('device-management.template-number')}}<span class="text-danger">*</span>
                </template>
                <b-form-input type="text" v-model="basicForm.archivesTemplateNumber"
                              :disabled="pageStatus==='show' || pageStatus==='edit'"
                              :state="!$v.basicForm.archivesTemplateNumber.$dirty ? null : !$v.basicForm.archivesTemplateNumber.$invalid"
                              :placeholder="$t('device-management.template-number-placeholder')"/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.archivesTemplateNumber.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group>
                <template slot="label">{{$t('device-management.template')}}<span class="text-danger">*</span>
                </template>
                <b-form-input type="text" v-model="basicForm.templateName"
                              :state="!$v.basicForm.templateName.$dirty ? null : !$v.basicForm.templateName.$invalid"
                              :placeholder="$t('device-management.template-name-placeholder')"/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.templateName.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group>
                <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                </template>
                <b-form-select v-model="basicForm.categoryId" :options="categorySelectOptions"
                               :disabled="pageStatus === 'show' || basicForm.status === '1000000701'"
                               :state="!$v.basicForm.categoryId.$dirty ? null : !$v.basicForm.categoryId.$invalid"
                               :placeholder="$t('device-management.device-classify-placeholder')" plain/>
                <div class="invalid-feedback d-block">
                  {{ (submitted && !$v.basicForm.categoryId.required) ?
                  $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                </div>
              </b-form-group>
              <b-form-group :label="$t('device-management.manufacture')">
                <b-form-select v-model="basicForm.manufacturer" :options="manufacturerOptions"
                               :disabled="pageStatus === 'show'" plain/>
              </b-form-group>
              <b-form-group :label="$t('device-management.device-model')">
                <b-form-input type="text" v-model="basicForm.originalModel"
                              :state="!$v.basicForm.originalModel.$dirty ? null : !$v.basicForm.originalModel.$invalid"
                              :placeholder="$t('device-management.origin-model-placeholder')"/>
              </b-form-group>
              <div v-if="getLocale()==='zh'" style="left: 3%;top: 10px">
                <img v-if="pageStatus === 'create'" src="../../../assets/img/no_active_stamp.png">
                <img v-if="basicForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                <img v-else-if="basicForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
              </div>
              <div v-if="getLocale()==='en'" style="left: 3%;top: 10px">
                <img v-if="pageStatus === 'create'" src="../../../assets/img/no_active_stamp_en.png">
                <img v-if="basicForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
                <img v-else-if="basicForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
              </div>
            </b-col>
            <b-col xxs="12" md="8" lg="9">
              <b-row class="h-100">
                <b-col cols="12" class="d-flex justify-content-between mb-2">
                  <label class="font-weight-bold" style="line-height: 28px">{{$t('device-management.document-template.device-show-list')}}</label>
                </b-col>
                <b-col v-if="pageStatus!=='show'" cols="12">
                  <b-row>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.name')">
                        <b-form-input type="text" v-model="indicatorForm.indicatorsName"/>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.unit')">
                        <b-form-input type="text" v-model="indicatorForm.indicatorsUnit"/>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group :label="$t('device-management.indicator.isNull')">
                        <b-form-select v-model="indicatorForm.isNull" :options="yesNoOptions" plain/>
                      </b-form-group>
                    </b-col>
                    <b-col class="d-flex align-items-center">
                      <div style="margin-top: 0.3rem">
                        <b-button size="sm" variant="success default" @click="onSaveIndicator()"
                                  :disabled="checkPermItem('device_indicator_create')">
                          <i class="icofont-plus"/> {{$t('device-management.document-template.add-indicator')}}
                        </b-button>
                      </div>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col v-if="pageStatus!=='show'" cols="12" class="table-responsive text-center" style="height: 75%">
                  <div class="table-wrapper table-responsive">
                    <vuetable
                      ref="indicatorTable"
                      :api-mode="false"
                      :fields="indicatorTableItems.fields"
                      :data-manager="indicatorTableDataManager"
                      :per-page="indicatorTableItems.perPage"
                      pagination-path="pagination"
                      @vuetable:pagination-data="onIndicatorTablePaginationData"
                      class="table-striped text-center"
                    >
                      <div slot="number" slot-scope="props">
                        <span class="cursor-p text-primary">{{ props.rowData.indicatorsName }}</span>
                      </div>
                      <div slot="required" slot-scope="props">
                        <b-button v-if="props.rowData.isNull === '1000000601'"
                                  :disabled="checkPermItem('device_indicator_update_is_null') || pageStatus==='show'"
                                  size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                  variant="success default">
                         &nbsp;{{$t('device-management.document-template.yes')}}
                        </b-button>
                        <b-button v-if="props.rowData.isNull === '1000000602'"
                                  :disabled="checkPermItem('device_indicator_update_is_null') || pageStatus==='show'"
                                  size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                  variant="light default">
                          {{$t('device-management.document-template.no')}}
                        </b-button>
                      </div>
                      <div slot="action" slot-scope="props">
                        <b-button
                          size="sm" @click="onDeleteIcon(props.rowData,props.rowIndex)"
                          :disabled="checkPermItem('device_indicator_delete') || pageStatus==='show'"
                          variant="danger default btn-square">
                          <i class="icofont-bin"/>
                        </b-button>
                      </div>
                    </vuetable>
                  </div>

                  <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="indicatorTablePagination"
                    @vuetable-pagination:change-page="onIndicatorTableChangePage"
                  />
                  </div>
                </b-col>
                <b-col v-else cols="12" class="table-responsive text-center" style="height: 100%">
                  <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="indicatorTable"
                    :api-mode="false"
                    :fields="indicatorTableItems.fields"
                    :data-manager="indicatorTableDataManager"
                    :per-page="indicatorTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onIndicatorTablePaginationData"
                    class="table-striped text-center"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary">{{ props.rowData.indicatorsName }}</span>
                    </div>
                    <div slot="required" slot-scope="props">
                      <b-button v-if="props.rowData.isNull === '1000000601'"
                                :disabled="checkPermItem('device_indicator_update_is_null') || pageStatus==='show'"
                                size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                variant="success default">
                        &nbsp;{{$t('device-management.document-template.yes')}}
                      </b-button>
                      <b-button v-if="props.rowData.isNull === '1000000602'"
                                :disabled="checkPermItem('device_indicator_update_is_null') || pageStatus==='show'"
                                size="xs" @click="onSwitchIsNull(props.rowData,props.rowIndex)"
                                variant="light default">
                        {{$t('device-management.document-template.no')}}
                      </b-button>
                    </div>
                    <div slot="action" slot-scope="props">
                      <b-button
                        size="sm" @click="onDeleteIcon(props.rowData,props.rowIndex)"
                        :disabled="checkPermItem('device_indicator_delete') || pageStatus==='show'"
                        variant="danger default btn-square">
                        <i class="icofont-bin"/>
                      </b-button>
                    </div>
                  </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="indicatorTablePagination"
                      @vuetable-pagination:change-page="onIndicatorTableChangePage"
                    />
                  </div>
                </b-col>
              </b-row>
            </b-col>
          </b-row>
          <b-row class="flex-grow-1 align-items-end">
            <b-col cols="12 text-right mt-3">
              <b-button size="sm" v-if="pageStatus !== 'show'" @click="onSaveTemplate()" variant="info default"><i
                class="icofont-save"/>
                {{$t('device-management.save')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000701' && pageStatus !=='create'"
                        @click="onAction('inactivate',basicForm)" size="sm"
                        variant="warning default" :disabled="checkPermItem('device_template_update_status')">
                <i class="icofont-ban"/> {{$t('system-setting.status-inactive')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000702' && pageStatus !=='create'"
                        @click="onAction('activate',basicForm)" size="sm"
                        :disabled="checkPermItem('device_template_update_status')"
                        variant="success default">
                <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button v-if="basicForm.status === '1000000702'&& pageStatus ==='edit'"
                        @click="onAction('delete',basicForm)" size="sm"
                        :disabled="checkPermItem('device_template_delete')"
                        variant="danger default">
                <i class="icofont-bin"/> {{$t('system-setting.delete')}}
              </b-button>
              <b-button size="sm" @click="onAction('show-list')" variant="info default"><i
                class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
              </b-button>
            </b-col>
          </b-row>

        </div>
      </div>

    </b-card>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered id="modal-active" ref="modal-active" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-active-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000701')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-active')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete-indicator" ref="modal-delete-indicator" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="onRemoveIndicator(indicatorDataDel, indicatorId)" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete-indicator')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

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
        <b-button size="sm" variant="orange default" @click="onExport()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="expz;zztModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
  </div>
</template>
<script>
  import _ from 'lodash';
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {downLoadFileFromServer, getApiManager, getApiManagerError, printFileFromServer} from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDicDataByDicIdForOptions, getDirection, getLocale} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  const {required, maxLength} = require('vuelidate/lib/validators');

  let getManufacturerName = (options, value) => {
    let name = null;
    if (options == null || options.length === 0)
      return name;
    options.forEach(option => {
      if (option.value === value)
        name = option.text;
    });
    return name;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-select': vSelect,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
      basicForm: {
        templateName: {
          required
        },
        archivesTemplateNumber: {
          required
        },
        categoryId: {
          required
        },
        originalModel:{
          maxLength: maxLength(20),
        }
      }
    },
    data() {
      return {
        isLoading: false,
        submitted: false,
        categoryData: [],
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        renderedCheckList:[],
        indicatorDataDel:null,
        indicatorId : null,
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        pageStatus: 'list',
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
        filterOption: {
          templateName: '',
          status: null,
          categoryId: null
        },
        basicForm: {
          templateName: '',
          archivesTemplateNumber: '',
          categoryId: '',
          manufacturer: '',
          originalModel: '',
          archiveIndicatorsList: [],
          note: '',
          status:'',
          archivesTemplateId: 0
        },
        categoryFilterData: [],
        categorySelectOptions: [],
        manufacturerOptions: [],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/device-management/document-template/archive-template/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'archivesTemplateNumber',
              title: this.$t('device-management.template-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'templateName',
              title: this.$t('device-management.template'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000701": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "1000000702": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }

            },
            {
              name: 'category',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturerName',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'originalModel',
              title: this.$t('device-management.device-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '210px'
            }
          ]
        },

        yesNoOptions: [
          {value: '1000000601', text: this.$t('system-setting.parameter-setting.yes')},
          {value: '1000000602', text: this.$t('system-setting.parameter-setting.none')},
        ],
        indicatorForm: {
          indicatorsId: 0,
          indicatorsName: null,
          indicatorsUnit: null,
          isNull: "1000000601"
        },

        indicatorTableItems: {
          fields: [
            {
              name: 'indicatorsId',
              sortField: 'indicatorsId',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'list-item-heading'
            },
            {
              name: '__slot:number',
              sortField: 'indicatorsName',
              title: this.$t('device-management.document-template.indicator'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '40%'
            },
            {
              name: 'indicatorsUnit',
              sortField: 'indicatorsUnit',
              title: this.$t('device-management.document-template.unit'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: '__slot:required',
              sortField: 'isNull',
              title: this.$t('device-management.document-template.required'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:action',
              title: this.$t('device-management.action'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage: 10
        },
        indicatorData: [],
      }
    },
    mounted() {
      this.getCategoryData();
      this.getManufacturerOptions();
      this.$refs.vuetable.$parent.transform = this.transformTemplateTable.bind(this);
    },
    methods: {
      getLocale() {
        return getLocale();
      },
      selectAll(value){
        this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.vuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.vuetable.selectedTo;
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
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManufacturerOptions() {
        this.manufacturerOptions = getDicDataByDicIdForOptions(9);
      },
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'locale' : getLocale(),
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        this.link = `device-management/document-template/archive-template`;
        this.name = 'document-template';
        this.isModalVisible = true;
      },
      onExport() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-template/archive-template`;
        if (this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'document-template', this.fileSelection);
          this.hideModal('model-export')
        }
      },
      onPrintButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/document-template/archive-template`;
        printFileFromServer(link, params);
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      getCategoryData() {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-classify/category/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.categoryData = data;

              break;
          }
        });
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          templateName: '',
          status: null,
          categoryId: null
        };
      },
      onAction(value, data = null) {

        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            this.initialize(data);
            break;
          case 'edit':
            this.pageStatus = 'edit';
            this.initialize(data);
            break;
          case 'show':
            this.pageStatus = 'show';
            this.initialize(data);
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'activate':
            this.initialize(data, false);
            this.$refs['modal-active'].show();
            //this.updateItemStatus('1000000701');
            break;
          case 'inactivate':
            this.initialize(data, false);
            //this.updateItemStatus('1000000702');
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.initialize(data, false);
            this.$refs['modal-delete'].show();
            break;
        }
      },
      transformTemplateTable(response) {
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
          this.renderedCheckList.push(data.data[i].archivesTemplateId);
          temp.category = temp.deviceCategory.categoryName;
          temp.manufacturerName = getManufacturerName(this.manufacturerOptions, temp.manufacturer);
          transformed.data.push(temp);
        }
        return transformed
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckList = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filterOption
        });
      },
      initialize(data = null, isUpdated = true) {
        this.indicatorForm = {
          indicatorsId: 0,
          indicatorsName: null,
          indicatorsUnit: null,
          isNull: "1000000602"
        };
        if (data == null) {
          this.basicForm = {
            templateName: '',
            archivesTemplateNumber: '',
            categoryId: '',
            manufacturer: '',
            originalModel: '',
            archiveIndicatorsList: [],
            note: '',
            archivesTemplateId: 0
          };
          this.indicatorData = [];
        } else {
          //this.basicForm = data;
          this.basicForm = {
            templateName: data.templateName,
            archivesTemplateNumber: data.archivesTemplateNumber,
            categoryId: data.categoryId,
            manufacturer: data.manufacturer,
            originalModel: data.originalModel,
            archiveIndicatorsList: data.archiveIndicatorsList,
            note: data.note,
            status: data.status,
            archivesTemplateId: data.archivesTemplateId
          };
          if (isUpdated) {
            let items = [];
            this.basicForm.archiveIndicatorsList.forEach((item) => {
              items.push({
                indicatorsId: item.indicatorsId,
                indicatorsName: item.indicatorsName,
                indicatorsUnit: item.indicatorsUnit,
                isNull: item.isNull
              });
              this.indicatorData = items;
            });
          }
        }
        this.submitted = false;
      },
      //save document template Item
      onSaveTemplate() {
        this.submitted = true;
        this.$v.basicForm.$touch();
        if (this.$v.basicForm.$invalid) {
          if(this.$v.basicForm.archivesTemplateNumber.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.template-number-placeholder`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.basicForm.templateName.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.template-name-placeholder`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.basicForm.categoryId.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-classify-placeholder`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        this.indicatorData.forEach((item) => {
          if (item.indicatorsId > 0)
            this.basicForm.archiveIndicatorsList.push(item.indicatorsId)
        });
        // if (this.basicForm.archiveIndicatorsList.length === 0) {
        //   this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.not-have-indicators`), {
        //     duration: 3000,
        //     permanent: false
        //   });
        //   return false;
        // }
        this.isLoading = true;
        let finalLink = this.basicForm.archivesTemplateId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/` + finalLink, {
            templateName: this.basicForm.templateName,
            archivesTemplateNumber: this.basicForm.archivesTemplateNumber,
            categoryId: this.basicForm.categoryId,
            manufacturer: this.basicForm.manufacturer,
            originalModel: this.basicForm.originalModel,
            archiveIndicatorsList: this.indicatorData,
            note: '',
            archivesTemplateId: this.basicForm.archivesTemplateId
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                if(finalLink === 'create') {
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.added-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }else{
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.updated-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }
                this.pageStatus = 'list';
                this.$refs.vuetable.reload();
                this.isLoading = false;
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-template-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-template-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-template-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-template-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
            this.isLoading = false;
          })
          .catch((error) => {
            this.isLoading = false;
          });
      },

      //update status
      updateItemStatus(statusValue) {
        let templateId = this.basicForm.archivesTemplateId;
        if (templateId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/update-status`, {
            archivesTemplateId: templateId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.basicForm.archivesTemplateId > 0)
                  this.basicForm.status = statusValue;

                  this.$refs.vuetable.reload();
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
        this.$refs['modal-active'].hide();
      },
      //remove
      removeItem() {
        let templateId = this.basicForm.archivesTemplateId;
        if (templateId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-template/delete`, {
            archivesTemplateId: templateId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.vuetable.refresh();
                if (this.basicForm.archivesTemplateId > 0)
                  initialize();
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['active-template']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.active-template`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
        this.$refs['modal-delete'].hide();
      },

      //todo for indicatorTable points.

      onIndicatorTablePaginationData(paginationData) {
        this.$refs.indicatorTablePagination.setPaginationData(paginationData)
      },
      onIndicatorTableChangePage(page) {
        this.$refs.indicatorTable.changePage(page)
      },
      indicatorTableDataManager(sortOrder, pagination) {
        let local = this.indicatorData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }
        pagination = this.$refs.indicatorTable.makePagination(
          local.length,
          this.indicatorTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.indicatorTableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      },
      onSaveIndicator() {
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/create`, this.indicatorForm)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorForm.indicatorsId = data;
                this.indicatorData.push(this.indicatorForm);
                this.indicatorForm = {
                  indicatorsId: 0,
                  indicatorsName: null,
                  indicatorsUnit: null,
                  isNull: "1000000601"
                };
                break;
            }
          })
          .catch((error) => {
          });
      },
      onSwitchIsNull(item, index) {
        let value = item.isNull === '1000000601' ? '1000000602' : '1000000601';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/update-isnull`, {
            indicatorsId: item.indicatorsId,
            isNull: value
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorData[index].isNull = value;
                item.isNull = value;
                break;
            }
          })
          .catch((error) => {
          });
      },

      onDeleteIcon(item, index){
        this.indicatorDataDel = item;
        this.indicatorId = index;
        this.$refs['modal-delete-indicator'].show();
      },
      onRemoveIndicator(item, index) {
        getApiManager()
          .post(`${apiBaseUrl}/device-management/document-template/archive-indicator/delete`, {
            indicatorsId: item.indicatorsId
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.indicator-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.indicatorData.splice(index, 1);
                break;
              case responseMessages['has-archives']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-archives`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
        this.$refs['modal-delete-indicator'].hide();
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.changeCheckAllStatus();
      },
      'indicatorTableItems.perPage': function (newVal) {

        this.$refs.indicatorTable.refresh();
      },

      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.categorySelectOptions = [];
        if (newVal.length === 0) {
          this.categorySelectOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        } else {
          this.categorySelectOptions = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.categoryFilterData = JSON.parse(JSON.stringify(this.categorySelectOptions));
        this.categoryFilterData.push({value: null, text: this.$t('permission-management.all')})
      },
      indicatorData(newVal, oldVal) {
        if (this.$refs.indicatorTable !== undefined)
          this.$refs.indicatorTable.refresh();
      }
    }
  }
</script>
<style>
  .img-rotate{
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }
</style>
