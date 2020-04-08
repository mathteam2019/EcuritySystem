<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('system-setting.site-list')">
        <b-row v-show="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('system-setting.site')">
                      <b-form-input v-model="filterOption.fieldDesignation"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.status')">
                      <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('system-setting.super-site')">
                      <b-form-select v-model="filterOption.parentFieldId" :options="superSiterFilterOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>

              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" @click="onSearchButton()" variant="info default">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onResetButton()" variant="info default">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('field_export')" @click="onExportButton()">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('field_print')" @click="onPrintButton()">
                    <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAction('create')" :disabled="checkPermItem('field_create')"
                            variant="success default">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="siteTableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    track-by="fieldId"
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onPaginationData"
                    class="table-striped"
                  >
                    <template slot="fieldSerial" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.fieldSerial }}</span>
                    </template>
                    <div slot="operating" slot-scope="props">

                      <b-button
                        size="sm" @click="onAction('edit',props.rowData)"
                        variant="primary default btn-square"
                        :disabled="checkPermItem('field_modify')">
                        <i class="icofont-edit"/>
                      </b-button>

                      <b-button
                        :disabled="checkPermItem('field_update_status')"
                        v-if="props.rowData.status === '1000000702'"
                        size="sm" @click="onAction('activate',props.rowData)"
                        variant="success default btn-square">
                        <i class="icofont-check-circled"/>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === '1000000701'"
                        size="sm" @click="onAction('inactivate',props.rowData)"
                        variant="warning default btn-square"
                        :disabled="props.rowData.parentFieldId === 0 || checkPermItem('field_update_status')"
                      >
                        <i class="icofont-ban"/>
                      </b-button>

                      <b-button
                        :disabled="props.rowData.status==='1000000701' || checkPermItem('field_delete')"
                        size="sm" @click="onAction('delete',props.rowData)"
                        variant="danger default btn-square"
                      >
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
          </b-col>
        </b-row>
        <b-row v-if="pageStatus !== 'table' &&pageStatus !== 'show' " class="h-100">
          <b-col cols="12 d-flex flex-column form-section " class="position-relative">
            <b-row>
              <b-col cols="6">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldSerial"
                                    :disabled="pageStatus==='edit'"
                                    :state="!$v.siteForm.fieldSerial.$dirty ? null : !$v.siteForm.fieldSerial.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-no')"/>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldDesignation"
                                    :state="!$v.siteForm.fieldDesignation.$dirty ? null : !$v.siteForm.fieldDesignation.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-name')"/>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="selectedParentSerial" disabled/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-select :disabled="pageStatus==='edit' && siteForm.status === '1000000701'" :options="superSiteOption"
                                     :state="!$v.siteForm.parentFieldId.$dirty ? null : !$v.siteForm.parentFieldId.$invalid"
                                     v-model="siteForm.parentFieldId" plain/>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.manager')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.leader"
                                    :placeholder="$t('system-setting.please-enter-manager')"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.system-phone')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.mobile" :state="!$v.siteForm.mobile.$dirty ? null : !$v.siteForm.mobile.$invalid"
                                    :placeholder="'000-0000-0000'"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group :label="$t('system-setting.remarks')">
                      <b-form-textarea rows="4" v-model="siteForm.note"
                                       :placeholder="$t('system-setting.please-enter-remarks')"/>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 align-items-end">
              <b-col cols="12" class="d-flex justify-content-end">
                <div class="mr-3">
                  <b-button @click="onAction('save')" size="sm" variant="success default">
                    <i class="icofont-save"/> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button v-if="siteForm.status === '1000000701' && pageStatus !== 'create'" :disabled="checkPermItem('field_update_status')"
                            @click="onAction('inactivate',siteForm)" size="sm" variant="warning default">
                    <i class="icofont-ban"/> {{$t('permission-management.action-make-inactive')}}
                  </b-button>
                  <b-button v-if="siteForm.status === '1000000702' && pageStatus !== 'create'" :disabled="checkPermItem('field_update_status')"
                            @click="onAction('activate',siteForm)" size="sm" variant="success default">
                    <i class="icofont-check-circled"/> {{$t('permission-management.active')}}
                  </b-button>
                  <b-button v-if="siteForm.status !== '1000000701' && pageStatus !== 'create'" :disabled="checkPermItem('field_delete')"
                            @click="onAction('delete',siteForm)" size="sm" variant="danger default">
                    <i class="icofont-bin"/> {{$t('permission-management.delete')}}
                  </b-button>
                  <b-button @click="onAction('list')" size="sm" variant="info default">
                    <i class="icofont-long-arrow-left"/> {{$t('system-setting.return')}}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <div class="position-absolute" style="bottom: 4%;left: 28%">
              <div v-if="getLocale()==='zh'"  class="position-absolute" style="bottom: 4%;left: 28%">
                <img v-if="pageStatus === 'create'" src="../../../assets/img/no_active_stamp.png">
                <img v-if="siteForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                <img v-else-if="siteForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
              </div>
              <div v-if="getLocale()==='en'" class="position-absolute" style="lbottom: 4%;left: 28%">
                <img v-if="pageStatus === 'create'" src="../../../assets/img/no_active_stamp_en.png">
                <img v-if="siteForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
                <img v-else-if="siteForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
              </div>
            </div>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus === 'show'" class="h-100">
          <b-col cols="12 d-flex flex-column form-section " class="position-relative">
            <b-row>
              <b-col cols="6">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input disabled type="text" v-model="siteForm.fieldSerial"
                                    :placeholder="$t('system-setting.please-enter-site-no')"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldDesignation"
                                    :placeholder="$t('system-setting.please-enter-site-name')"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="selectedParentSerial" disabled/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-select v-if="siteForm.parentFieldId !=0" :options="superSiteOption"
                                     v-model="siteForm.parentFieldId" disabled plain/>
                      <b-form-input v-else-if="siteForm.parentFieldId == 0" v-model="selectedParentSerial" type="text"
                                    disabled/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.manager')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.leader"
                                    :placeholder="$t('system-setting.please-enter-manager')"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.system-phone')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.mobile" :placeholder="'000-0000-0000'"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group :label="$t('system-setting.remarks')">
                      <b-form-textarea rows="4" v-model="siteForm.note"
                                       :placeholder="$t('system-setting.please-enter-remarks')"/>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 align-items-end">
              <b-col cols="12" class="d-flex justify-content-end">
                <div class="mr-3">
                  <b-button v-if="siteForm.status === '1000000701'"
                            @click="onAction('inactivate',siteForm)" size="sm"
                            variant="warning default" :disabled="checkPermItem('field_update_status')">
                    <i class="icofont-ban"/> {{$t('system-setting.status-inactive')}}
                  </b-button>
                  <b-button v-if="siteForm.status === '1000000702'" :disabled="checkPermItem('field_update_status')"
                            @click="onAction('activate',siteForm)" size="sm" variant="success default">
                    <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
                  </b-button>
<!--                  <b-button v-if="siteForm.status === '1000000702'" :disabled="checkPermItem('field_delete')"-->
<!--                            @click="onAction('delete',siteForm)" size="sm"-->
<!--                            variant="danger default">-->
<!--                    <i class="icofont-bin"/> {{$t('system-setting.delete')}}-->
<!--                  </b-button>-->
                  <b-button @click="onAction('list')" size="sm" variant="info default">
                    <i class="icofont-long-arrow-left"/> {{$t('system-setting.return')}}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <div v-if="getLocale()==='zh'"  class="position-absolute" style="bottom: 4%;left: 28%">
              <img v-if="siteForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
              <img v-else-if="siteForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
            </div>
            <div v-if="getLocale()==='en'" class="position-absolute" style="lbottom: 4%;left: 28%">
              <img v-if="siteForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
              <img v-else-if="siteForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
            </div>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('system-setting.site-architecture')">
        <b-row>
          <b-col cols="12">
            <div class="text-center">
              <vue2-org-tree
                :data="treeData"
                :horizontal="false"
                :collapsable="false"
                :label-class-name="treeLabelClass"
                :render-content="renderTreeContent"
              />
            </div>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('site-management.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered id="modal-active" ref="modal-active" :title="$t('system-setting.prompt')">
      {{$t('site-management.make-active-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000701')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-active')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('site-management.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
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
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
  </div>
</template>

<script>

  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import Vue2OrgTree from '../../../components/vue2-org-tree'
  import {validationMixin} from 'vuelidate';

  import {apiBaseUrl} from "../../../constants/config";
  import {
    downLoadFileFromServer,
    getApiManager,
    getApiManagerError,
    isPhoneValid,
    printFileFromServer
  } from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  const {required, maxLength} = require('vuelidate/lib/validators');


  let getParentSerialName = (siteData, fieldId) => {
    let parentSerialNumber = null;
    if (siteData == null || siteData.length === 0)
      return parentSerialNumber;
    siteData.forEach(siteItem => {
      if (siteItem.fieldId === fieldId)
        parentSerialNumber = siteItem.fieldSerial;
    });
    return parentSerialNumber;
  };

  let fnGetOrgLevel = orgData => {
    let level = 0;
    if (orgData == null)
      return level;
    while (orgData.parent != null) {
      level++;
      orgData = orgData.parent;
    }
    return level;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-select': vSelect,
      Vue2OrgTree,
      Modal
    },
    mixins: [validationMixin],
    mounted() {

      ///////////////////////////////////////////////////////////
      ////////////// Load site list from server /////////////////
      ///////////////////////////////////////////////////////////
      this.handleWindowResize();
      this.getSiteData();

      this.$refs.vuetable.$parent.transform = this.transformSiteTable.bind(this);
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.changeCheckAllStatus();
      },
      'siteForm.parentFieldId': function (newVal) {
        this.selectedParentSerial = getParentSerialName(this.siteData, newVal);
        if (this.selectedParentSerial === null)
          this.selectedParentSerial = this.$t('system-setting.none');
      },

      siteData: function (newVal, oldVal) {
        let nest = (items, id = 0, depth = 1) =>
          items
            .filter(item => item.parentFieldId == id)
            .map(item => ({
              ...item,
              children: nest(items, item.fieldId, depth + 1),
              id: id++,
              label: `<div class="org-content-top"><span>${depth}</span>${nameLabel(item.fieldSerial)}</div><div class="org-content-bottom">${item.fieldDesignation}</div>`
            }));
        let nameLabel = (orgNumber) => {
          //console.log(orgNumber)
          if(orgNumber.toString().length>7) {
            orgNumber = orgNumber.substring(0, 7) + '...';
          }
          //console.log(orgNumber);
          return orgNumber;
        }
        this.treeData = nest(newVal)[0];
        this.changeOrgTree(this.treeData.children, 1);
        this.superSiteOption.unshift({
          text: this.treeData.fieldDesignation,
          value: this.treeData.fieldId
        });
        this.superSiterFilterOptions = [];
        this.superSiteOption.forEach(site => {
            this.superSiterFilterOptions.push(site);
        })
          this.superSiterFilterOptions.unshift({value: null, text: this.$t('permission-management.all')});
        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].fieldId == org.parentFieldId) {
                return newVal[i];
              }
            }
            return null;
          };
          let stepValue = org;
          let level = 0;
          while (getParent(stepValue) !== null) {
            stepValue = getParent(stepValue);
            level++;
          }
          return level;
        };
        let generateSpace = (count) => {
          let string = '';
          while (count--) {
            string += '&nbsp;&nbsp;&nbsp;&nbsp;';
          }
          return string;
        };
        this.superSiteOptions = [];
        this.superSiteOptions = newVal.map(site => ({
          value: site.fieldId,
          html: `${generateSpace(getLevel(site))}${site.fieldDesignation}`
        }));
        if (this.superSiteOptions.length === 0)
          this.superSiteOptions.push({
            text: this.$t('system-setting.none'),
            value: 0
          });

      }
    },
    data() {
      return {
        isLoading: false,
        selectedSiteItem: null,
        siteData: [],
        link: '',
        params: {},
        name: '',
        fileSelection : [],
        renderedCheckList:[],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        selectedParentSerial: '',
        filterOption: {
          fieldDesignation: '',
          status: null,
          parentFieldId: null
        },

        showLength:20,
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/site-management/field/get-by-filter-and-page`,
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
              dataClass: 'text-center',
              width: '4%'
            },
            {
              name: '__slot:fieldSerial',
              sortField: 'fieldSerial',
              title: this.$t('system-setting.site-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: 'fieldDesignation',
              title: this.$t('system-setting.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '10%'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%',
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
              name: 'parentFieldSerial',
              title: this.$t('system-setting.super-site-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'parentFieldDesignation',
              title: this.$t('system-setting.super-site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'leader',
              title: this.$t('system-setting.manager'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%'
            },
            {
              name: 'mobile',
              title: this.$t('system-setting.system-phone'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '10%'
            },
            {
              name: 'noteLabel',
              title: this.$t('system-setting.remarks'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%',
              callback: (value) => {
                if(value === null) return '';
                if(value.isLong === false) return value.groupMember;
                else{
                  return this.hoverContent(value);
                }
              },
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16px'
            }
          ]
        },
        stateOptions: [
          {value: null, text: this.$t('system-setting.status-all')},
          {value: "1000000701", text: this.$t('system-setting.status-active')},
          {value: "1000000702", text: this.$t('system-setting.status-inactive')},
        ],
        pageStatus: 'table', // table, create, edit, show
        selectedSite: '0000',
        superSiteOptions: [],
        superSiterFilterOptions: [],
        superSiteOption: [],
        treeData: {},
        siteForm: {
          status:'',
          fieldId: 0,
          fieldSerial: '',
          fieldDesignation: '',
          parentFieldId: '',
          parentFieldDesignation: '',
          leader: '',
          mobile: '',
          note: ''
        }
      }
    },
    validations: {
      fileSelection : {
        required
      },
      siteForm: {
        fieldSerial: {
          required, maxLength: maxLength(50),
        },
        fieldDesignation: {
          required, maxLength: maxLength(50),
        },
        parentFieldId: {
          required
        },
        mobile: {
          isPhoneValid
        }
      }
    },
    methods: {
      handleWindowResize(event) {
        const windowWidth = window.innerWidth;
        console.log(windowWidth);
        if(windowWidth<=1280) {
          this.showLength = 10;
        }
      },
      hoverContent(value) {
        let content = '<div class="item-wrapper slide-right">\n' +
          '      <span class="item d-flex flex-column">\n' + value.label +
          '      </span>\n' +
          '      <div class="item-extra-info flex-column d-flex">\n' + value.groupMember +
          '      </div>\n' +
          '    </div>';
        return content;
      },

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
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        this.link = `site-management/field`;
        this.name = 'site';
        this.isModalVisible = true;
      },
      onExport(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `site-management/field`;
        if(this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'site', this.fileSelection);
          this.hideModal('model-export')
        }
      },
      onPrintButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `site-management/field`;
        printFileFromServer(link, params);
      },

      getSiteData() {
        getApiManagerError().post(`${apiBaseUrl}/site-management/field/get-all-field`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        });
      },
      generatSpace(count) {
        let string = '';
        while (count--) {
          string += '&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        return string;
      },

      changeOrgTree(treeData, index) {


        if (!treeData || treeData.length === 0) {
          return;
        }

        let tmp = treeData;

        for (let i = 0; i < tmp.length; i++) {
          this.changeOrgTree(tmp[i].children, index + 1);

          this.superSiteOption.unshift({
            value: tmp[i].fieldId,
            html: `${this.generatSpace(index)}${tmp[i].fieldDesignation}`
          });
        }
      },
      onAction(value, data = null) {
        switch (value) {
          case 'create':
            this.initialize();
            this.pageStatus = 'create';
            break;
          case 'save':
            this.saveSiteItem();
            break;
          case 'edit':
            this.initialize(data);
            this.pageStatus = 'edit';
            break;
          case 'activate':
            this.siteForm = data;
            this.$refs['modal-active'].show();
            //this.updateItemStatus('1000000701');
            break;
          case 'show':
            this.initialize(data);
            this.pageStatus = 'show';
            break;
          case 'inactivate':
            this.initialize(data);
            //this.updateItemStatus('1000000702');
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.initialize(data);
            this.$refs['modal-delete'].show();
            break;
          case 'list':
            this.pageStatus = 'table';
            break;
        }
      },
      initialize(data = null) {
        if (data == null) {
          this.siteForm = {
            status:'',
            fieldSerial: '',
            fieldDesignation: '',
            parentFieldId: '',
            parentFieldDesignation: '',
            leader: '',
            mobile: '',
            note: '',
            fieldId: 0
          };
          this.selectedSiteItem = '';
        } else {
          // this.siteForm = data;
          this.siteForm = {
            status: data.status,
            fieldSerial: data.fieldSerial,
            fieldDesignation: data.fieldDesignation,
            parentFieldId: data.parentFieldId,
            parentFieldDesignation: data.parentFieldDesignation,
            leader: data.leader,
            mobile: data.mobile,
            note: data.note,
            fieldId: data.fieldId
          };
          this.selectedSiteItem = getParentSerialName(this.siteData, this.siteForm.parentFieldId);
        }

      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          fieldDesignation: '',
          status: null,
          parentFieldId: null
        };
      },
      /**
       * save new site item
       */
      saveSiteItem() {
        //this.submitted = true;
        this.$v.siteForm.$touch();
        if (this.$v.siteForm.$invalid) {
          if(this.$v.siteForm.fieldSerial.$invalid){
            if(this.siteForm.fieldSerial === ''){
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.please-enter-site-no`), {
                duration: 3000,
                permanent: false
              });
            }
            else  {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.length-valid-site-no`), {
                duration: 3000,
                permanent: false
              });
            }

            return;
          }
          if(this.$v.siteForm.fieldDesignation.$invalid){
            if(this.siteForm.fieldDesignation === ''){
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.please-enter-site-name`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.length-valid-site-name`), {
                duration: 3000,
                permanent: false
              });
            }

            return;
          }
          if(this.$v.siteForm.parentFieldId.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.please-select-parent-organization`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.siteForm.mobile.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-mobile`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        this.isLoading = true;
        let finalLink = this.siteForm.fieldId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/` + finalLink, this.siteForm)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {

              case responseMessages['ok']: // okay
                if(finalLink === 'create') {
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.site-added-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }
                else {
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.site-updated-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }
                this.pageStatus = 'table';
                this.getSiteData();
                this.$refs.vuetable.reload();
                this.isLoading = false;
                break;
              case responseMessages["has-devices"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-field-designation']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-field-designation`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-field-serial']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-field-serial`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-children']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-children`), {
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

        this.$v.siteForm.$reset();

      },


      siteTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        this.renderedCheckList = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filterOption
        });
      },
      transformSiteTable(response) {
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
          this.renderedCheckList.push(data.data[i].fieldId);
          temp.parentFieldSerial = temp.parent != null ? temp.parent.fieldSerial : null;
          temp.parentFieldDesignation = temp.parent != null ? temp.parent.fieldDesignation : null;

          if(temp.note) {
            let note = temp.note.toString();
            if (note.length > 20) {
              temp.note = note.substr(0, 20) + "···"; // Gets the first part
            }
            let isLong = false;
            if(note.length>this.showLength){
              isLong = true;
              temp.noteLabel = {
                groupMember : note,
                label : note.substr(0, this.showLength) + '...',
                isLong : isLong
              };
            }
            else {
              temp.noteLabel = {
                groupMember : note,
                isLong : isLong
              };
            }
          }


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

      updateItemStatus(statusValue) {
        let fieldId = this.siteForm.fieldId;
        if (fieldId === 0)
          return false;

        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/update-status`, {
            fieldId: fieldId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.siteForm.fieldId > 0)
                  this.siteForm.status = statusValue;
                  this.$refs.vuetable.reload();
                this.getSiteData();
                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages["has-devices"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-devices`), {
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

      removeItem() {
        let fieldId = this.siteForm.fieldId;
        if (fieldId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/delete`, {
            fieldId: fieldId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.site-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'table';
                if (this.siteForm.fieldId > 0)
                  this.siteForm = null;
                this.$refs.vuetable.refresh();
                this.getSiteData();
                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages["has-devices"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages["active-field"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.active-field`), {
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

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      renderTreeContent: function (h, data) {
        return h('div', {
            domProps: {
              innerHTML: data.label
            }
          }
        );
      },
      treeLabelClass: function (data) {
        let level = fnGetOrgLevel(data);
        const labelClasses = ['bg-level-1', 'bg-level-2', 'bg-level-3', 'bg-level-4', 'bg-level-5'];
        return `${labelClasses[level % 5]} text-white`;
      }
    }
  }
</script>
<style lang="scss">
  .item-wrapper {
    position: relative;
    height: fit-content !important;
  //padding-left: $item-padding;
    display: inline-block;
    width: 100%;

  //height: 100%;
    /*& > .item {*/
    /*  z-index: 1;*/
    /*  position: relative;*/
    /*  height: 100%;*/
    /*  width: 100%;*/
    /*  display: inline-block;*/
    /*  cursor: pointer;*/
    /*  !*&:hover {*!*/
    /*  !*  box-shadow: 1px 2px 0 #c6c6c6;*!*/
    /*  !*}*!*/
    /*  !*&.active {*!*/
    /*  !*  .item-header {*!*/
    /*  !*    border-bottom-color: #009900;*!*/
    /*  !*  }*!*/
    /*  !*}*!*/
    /*  .item-header {*/
    /*    background: #f3f3f3;*/
    /*    border-bottom: solid 2px #c6c6c6;*/
    /*    height: calculateRem(50px);*/
    /*    display: flex;*/
    /*    justify-content: space-between;*/
    /*    align-items: center;*/
    /*    padding: 0 calculateRem(20px) 0 calculateRem(20px);*/
    /*    .label {*/
    /*      white-space: pre;*/
    /*      font-size: calculateRem(15px);*/
    /*      color: #666666;*/
    /*      max-width: 100%;*/
    /*      flex-grow: 1;*/
    /*      text-overflow: ellipsis;*/
    /*      overflow: hidden;*/
    /*    }*/
    /*    .action-list {*/
    /*      white-space: pre;*/
    /*      img {*/
    /*        width: calculateRem(20px);*/
    /*        margin-left: 0.5rem;*/
    /*        &.disabled {*/
    /*          filter: grayscale(1);*/
    /*        }*/
    /*        img:first-child {*/
    /*          margin-left: 0;*/
    /*        }*/
    /*      }*/

    /*    }*/
    /*  }*/
    /*  .item-body {*/
    /*    padding: calculateRem(10px);*/
    /*    .left-side {*/
    /*      .action {*/
    /*        button.btn {*/
    /*          margin-bottom: calculateRem(10px);*/
    /*          white-space: pre;*/
    /*          font-size: calculateRem(11px);*/
    /*          &.btn-success {*/
    /*            background-color: #49cf6f;*/
    /*            border-color: #49cf6f;*/
    /*            &:hover {*/
    /*              background-color: darken(#49cf6f, 8%);*/
    /*              border-color: darken(#49cf6f, 8%);*/
    /*            }*/
    /*          }*/
    /*          &.btn-info {*/
    /*            background-color: #1782d4;*/
    /*            &:hover {*/
    /*              background-color: darken(#1782d4, 8%);*/
    /*              border-color: darken(#1782d4, 8%);*/
    /*            }*/
    /*          }*/
    /*        }*/
    /*      }*/
    /*      .img {*/
    /*        flex-grow: 1;*/
    /*        width: 65px;*/
    /*        height: 94px;*/
    /*        display: flex;*/
    /*        align-items: center;*/
    /*        img {*/
    /*          width: 90%;*/
    /*          object-fit: contain;*/
    /*        }*/
    /*      }*/
    /*    }*/
    /*    .right-side {*/
    /*      .text-top {*/
    /*        color: #1782d4;*/
    /*        font-weight: bold;*/
    /*        margin-bottom: calculateRem(15px);*/
    /*      }*/
    /*      .content {*/
    /*        & > div {*/
    /*          display: flex;*/
    /*          label {*/
    /*            white-space: pre;*/
    /*            overflow: visible;*/
    /*            text-overflow: ellipsis;*/
    /*            max-width: 100%;*/
    /*            color: #606266;*/
    /*            font-size: calculateRem(12px);*/
    /*            line-height: calculateRem(12px);*/
    /*            &:first-child {*/
    /*              width: 37%;*/
    /*              min-width: 37%;*/
    /*            }*/
    /*            &:last-child {*/
    /*              flex-grow: 1;*/
    /*            }*/
    /*            &.disabled {*/
    /*              color: #c0c0c0;*/
    /*            }*/
    /*          }*/
    /*        }*/

    /*      }*/

    /*      .caption {*/
    /*        width: 37%;*/
    /*      }*/
    /*    }*/
    /*  }*/
    /*}*/
    & > .item-extra-info {
      overflow-wrap: break-word;
      padding: calculateRem(18px);
      opacity: 0;
      transition: 0ms;
      display: none !important;
      border-radius: 0.3rem;
      position: absolute;
      top: 0;
      width: 80%;
      left: calculateRem(30px);
      background: wheat;
      z-index: 1;
      /*& > div {*/
      /*  & > div {*/
      /*    margin-bottom: calculateRem(4px);*/
      /*    align-items: center;*/
      /*    &:first-child {*/
      /*      width: calculateRem(75px);*/
      /*      margin-bottom: 0;*/
      /*      font-size: 0.7rem;*/
      /*      color: white;*/
      /*      white-space: pre;*/
      /*      overflow: hidden;*/
      /*      text-overflow: ellipsis;*/
      /*    }*/
      /*    &:last-child {*/
      /*      display: flex;*/
      /*      align-items: center;*/
      /*      flex-grow: 1;*/
      /*      color: white;*/
      /*      white-space: pre;*/
      /*      overflow: hidden;*/
      /*      text-overflow: ellipsis;*/
      /*      img {*/
      /*        width: calculateRem(12px);*/
      /*      }*/
      /*      span {*/
      /*        font-size: 0.7rem;*/
      /*        &.success {*/
      /*          color: #42b662;*/
      /*        }*/
      /*        &.pending {*/
      /*          color: #bbbbbb;*/
      /*        }*/
      /*        &.danger {*/
      /*          color: #e12c48;*/
      /*        }*/
      /*        margin-left: calculateRem(5px);*/
      /*        &.without {*/
      /*          margin-left: calculateRem(18px);*/
      /*        }*/
      /*      }*/
      /*      .chart-container {*/
      /*        width: 100%;*/
      /*        height: 100%;*/
      /*      }*/
      /*    }*/
      /*  }*/
      /*}*/
    }
    /*&.slide-left {*/
    /*  & > .item-extra-info {*/
    /*    left: 0;*/
    /*  }*/
    /*  &:hover {*/
    /*    & > .item-extra-info {*/
    /*      left: calc(1.25rem - 100%);*/
    /*    }*/

    /*  }*/
    /*}*/
    &:hover {
      & > .item {
        z-index: 4;
      }
      & > .item-extra-info {
        top: -0.5rem;
        padding: 0.5rem;
      //position: fixed;
        display: inline-block !important;
        opacity: 1;
        transition: 10ms;
        left: 100%;
        z-index: 1;
      }

    }
  }
  .col-form-label {
    margin-bottom: 1px;
  }
  .img-rotate{
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }
</style>
