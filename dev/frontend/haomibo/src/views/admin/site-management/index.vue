<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>

    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('system-setting.site-list')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>
                  <b-col >
                    <b-form-group :label="$t('system-setting.site')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col >
                    <b-form-group :label="$t('system-setting.status-active')">
                      <b-form-select :options="stateOptions" plain />
                    </b-form-group>
                  </b-col>

                  <b-col >
                    <b-form-group :label="$t('system-setting.super-site')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>

              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onNewClicked()" variant="success default">
                    <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
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

                    <template slot="siteNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onSiteTableRowClick(props.rowData)">{{ props.rowData.siteNumber }}</span>
                    </template>

                    <div slot="operating" slot-scope="props">

                      <b-button
                        v-if="props.rowData.status === 'active'"
                        size="sm"
                        variant="primary default btn-square"
                        disabled>
                        <i class="icofont-edit"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'inactive'"
                        size="sm"
                        @click="editRow(props.rowData)"
                        variant="primary default btn-square">
                        <i class="icofont-edit"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'inactive'"
                        size="sm"
                        variant="success default btn-square">
                        <i class="icofont-check-circled"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'active'"
                        size="sm"
                        variant="warning default btn-square"
                        v-b-modal.modal-inactive>
                        <i class="icofont-ban"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'inactive+'"
                        size="sm"
                        variant="success default btn-square"
                        disabled>
                        <i class="icofont-ban"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status==='inactive'"
                        size="sm"
                        variant="danger default btn-square"
                        v-b-modal.modal-delete  >
                        <i class="icofont-bin"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status==='active'"
                        size="sm"
                        variant="primary default btn-square"
                        disabled>
                        <i class="icofont-bin"></i>
                      </b-button>
                    </div>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    :initial-per-page="vuetableItems.perPage"
                    @vuetable-pagination:change-page="onChangePage"
                  ></vuetable-pagination-bootstrap>

                  <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
                    {{$t('system-setting.make-inactive-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="inactiveRow('props.rowData')" class="mr-1">{{$t('system-setting.ok')}}</b-button>
                      <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}</b-button>
                    </template>
                  </b-modal>

                  <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
                    {{$t('system-setting.delete-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="deleteRow('props.rowData')" class="mr-1">{{$t('system-setting.ok')}}</b-button>
                      <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}</b-button>
                    </template>
                  </b-modal>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus !== 'table'" class="h-100">
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
                      <b-form-input type="text" v-model="siteForm.siteNumber"
                                    :state="!$v.siteForm.siteNumber.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-no')"></b-form-input>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}</b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.siteName"
                                    :state="!$v.siteForm.siteName.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-name')"></b-form-input>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}</b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="selectedStatus" disabled></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-select :options="superSiteOptions" v-model="selectedStatus" plain />
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.manager')}}
                      </template>
                      <b-form-input type="text" :placeholder="$t('system-setting.please-enter-manager')"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.system-phone')}}
                      </template>
                      <b-form-input type="text" :placeholder="''"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group :label="$t('system-setting.remarks')">
                      <b-form-textarea rows="4" :placeholder="$t('system-setting.please-enter-remarks')"></b-form-textarea>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 align-items-end">
              <b-col cols="12" class="d-flex justify-content-end">
                <div class="mr-3">
                  <b-button @click="" size="sm" variant="success default" v-if="pageStatus !== 'show'">
                    <i class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="" size="sm" variant="warning default" v-if="pageStatus !== 'create'">
                    <i class="icofont-ban"></i> {{$t('system-setting.status-inactive')}}
                  </b-button>
                  <b-button @click="" size="sm" variant="danger default" v-if="pageStatus !== 'create'">
                    <i class="icofont-bin"></i> {{$t('system-setting.delete')}}
                  </b-button>
                  <b-button @click="onReturnClicked" size="sm" variant="info default">
                    <i class="icofont-long-arrow-left"></i> {{$t('system-setting.return')}}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <div class="position-absolute" style="top: 10%;right: 10%">
              <img  src="../../../assets/img/no_active_stamp.png">
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
                @on-expand="() => {}"
                @on-node-click="() => {}"
              />
            </div>
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
    import { validationMixin } from 'vuelidate';
    const { required } = require('vuelidate/lib/validators');

    export default {
        components: {
            'input-tag' : InputTag,
            'v-select' : vSelect,
            'vuetable' : Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap' : VuetablePaginationBootstrap,
            Vue2OrgTree
        },
        mixins: [validationMixin],
        data () {
            return {
                selectedStatus: '',
                vuetableItems: {
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
                            title: this.$t('system-setting.no'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:siteNumber',
                            sortField: 'siteNumber',
                            title: this.$t('system-setting.site-no'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'site-name',
                            sortField: 'site-name',
                            title: this.$t('system-setting.site'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'status',
                            sortField: 'status',
                            title: this.$t('system-setting.status-active'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (value) => {
                                const dictionary = {
                                    "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                                    "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                                };
                                if(!dictionary.hasOwnProperty(value)) return '';
                                return dictionary[value];
                            }
                        },
                        {
                            name: 'super-site-no',
                            sortField: 'super-site-no',
                            title: this.$t('system-setting.super-site-no'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (value) => {
                                if(value) {
                                    return value;
                                } else {
                                    return this.$t('system-setting.none');
                                }
                            }
                        },
                        {
                            name: 'super-site-name',
                            sortField: 'super-site-name',
                            title: this.$t('system-setting.super-site'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                            callback: (value) => {
                                if(value) {
                                    return value;
                                } else {
                                    return this.$t('system-setting.none');
                                }
                            }
                        },
                        {
                            name: 'manager',
                            sortField: 'manager',
                            title: this.$t('system-setting.manager'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'contact-info',
                            sortField: 'contact-info',
                            title: this.$t('system-setting.system-phone'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'remarks',
                            sortField: 'remarks',
                            title: this.$t('system-setting.remarks'),
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
                        "siteNumber": "0000",
                        "site-name": "首都机场",
                        "status": "active",
                        "super-site-no": null,
                        "super-site-name": null,
                        "manager": "张三",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 2,
                        "siteNumber": "0100",
                        "site-name": "1号航站楼",
                        "status": "active",
                        "super-site-no": "0000",
                        "super-site-name": "总部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 3,
                        "siteNumber": "0200",
                        "site-name": "2号航站楼",
                        "status": "active",
                        "super-site-no": "0000",
                        "super-site-name": "总部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 4,
                        "siteNumber": "0201",
                        "site-name": "通道1",
                        "status": "active",
                        "super-site-no": "0200",
                        "super-site-name": "生产部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 5,
                        "siteNumber": "0202",
                        "site-name": "通道2",
                        "status": "inactive",
                        "super-site-no": "0200",
                        "super-site-name": "生产部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 6,
                        "siteNumber": "0300",
                        "site-name": "3号航站楼",
                        "status": "active",
                        "super-site-no": "0000",
                        "super-site-name": "总部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                    {
                        "no": 7,
                        "siteNumber": "0301",
                        "site-name": "通道001",
                        "status": "inactive",
                        "super-site-no": "0300",
                        "super-site-name": "销售部",
                        "manager": "",
                        "contact-info": "13800001234",
                        "remarks": "",
                    },
                ],
                stateOptions: [
                    {value: "all", text: this.$t('system-setting.status-all')},
                    {value: "valid", text: this.$t('system-setting.status-active')},
                    {value: "invalid", text: this.$t('system-setting.status-inactive')},
                ],
                pageStatus: 'table', // table, create, edit, show
                selectedSite: '0000',
                superSiteOptions: [
                    {value: "0000", text: '首都机场'},
                    {value: "0001", text: '1号航站楼'},
                    {value: "0002", text: '2号航站楼'},
                    {value: "0003", text: '通道1'},
                    {value: "0004", text: '通道2'},
                    {value: "0020", text: '3号航站楼'},
                    {value: "0030", text: '通道001'},
                ],
                treeData: {
                    id: 0,
                    label: '0000 首都机场',
                    children: [
                        {
                            id: 1,
                            label: '0100 1号航站楼'
                        },
                        {
                            id: 2,
                            label: '0200 2号航站楼',
                            children: [
                                {
                                    id: 3,
                                    label: '0201 通道1'
                                },
                                {
                                    id: 4,
                                    label: '0202 通道2'
                                }
                            ]
                        },
                        {
                            id: 5,
                            label: '0300 3号航站楼',
                            children: [
                                {
                                    id: 6,
                                    label: '0301 通道001'
                                }
                            ]
                        }
                    ]
                },
                siteForm: {
                    siteNumber: '',
                    siteName: '',
                    parentSiteNumber: '',
                    parentSiteName: '',
                    admin: '',
                    phone: '',
                    remarks: ''
                }
            }
        },
        validations: {
            siteForm: {
                siteNumber: {
                    required
                },
                siteName: {
                    required
                }
            }
        },
        methods: {
            onNewClicked() {
                this.pageStatus = 'create';
            },
            onPaginationData(paginationData) {
                this.$refs.pagination.setPaginationData(paginationData);
            },
            onChangePage(page) {
                this.$refs.vuetable.changePage(page);
            },
            onSiteTableRowClick(rowData) {
                this.pageStatus = 'show';
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
            editRow(data) {
                console.log(data);
                this.pageStatus = 'edit';
            },
            inactiveRow(data) {
                console.log(data);
                this.$refs['modal-inactive'].hide();
            },
            deleteRow(data) {
                console.log(data);
                this.$refs['modal-delete'].hide();
            },
            hideModal(modal) {
                this.$refs[modal].hide();
            },
            onReturnClicked() {
                this.pageStatus = 'table';
            },
            onHorizontalSubmit() {
                console.log('submit form');
            },
            renderTreeContent: function(h, data) {
                return data.label;
            },
            treeLabelClass: function(data) {
                const labelClasses = ['bg-primary', 'bg-secondary', 'bg-success', 'bg-info', 'bg-warning', 'bg-danger'];
                return `${labelClasses[data.id % 6]} text-white`;
            }
        }
    }
</script>
