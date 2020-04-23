<style lang="scss">
  .item-wrapper {
    position: relative;
    height: fit-content !important;
    //padding-left: $item-padding;
    display: inline-block;
    width: 100%;
    & > .item-extra-info {
      overflow-wrap: break-word;
      padding: calculateRem(18px);
      opacity: 0;
      transition: 0ms;
      display: none !important;
      border-radius: 0.3rem;
      position: absolute;
      top: 0;
      width: 90%;
      left: calculateRem(30px);
      background: wheat;
      z-index: 1;

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
  .assign-permission-management {
    .v-select.v-select-custom-style {
      & > div {
        border-radius: 0.3rem !important;

        & > div {
          border-radius: 0.3rem !important;
        }
      }

    }
  }
</style>
<template>
  <div class="assign-permission-management">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('permission-management.assign-permission-management.assign-to-user')"
             @click="tabStatus = 'user'">
        <b-row v-show="pageStatus === 'table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="7">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.user')">
                      <b-form-input v-model="userFilter.userName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.affiliated-org')">
                      <b-form-select :options="orgNameFilterData" v-model="userFilter.orgId" plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.role')">
                      <b-form-input v-model="userFilter.roleName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.data-range')">
                      <b-form-select v-model="userFilter.dataRange" :options="userDataRangeOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>

              <b-col cols="5" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onClickUserSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onClickUserResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('assign_user_export')" @click="onExportButton()">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('assign_user_print')" @click="onPrintUserButton()">
                    <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAssignUserCreatePage()"
                            :disabled="checkPermItem('assign_user_create')" variant="success default">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>

            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="userVuetable"
                    :api-url="userVuetableItems.apiUrl"
                    :fields="userVuetableItems.fields"
                    :per-page="userVuetableItems.perPage"
                    class="table-striped"
                    :http-fetch="userVuetableFetch"
                    pagination-path="userPagination"
                    track-by="userId"
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onUserPaginationData"
                  >
                    <template slot="userName" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onUserNameClicked(props.rowData)">
                        {{props.rowData.userName}}
                      </span>
                    </template>

                    <template slot="actions" slot-scope="props">
                      <div>
                        <b-button
                          size="sm"
                          :disabled="checkPermItem('assign_user_modify')"
                          variant="primary default btn-square"
                          @click="editUserRoles(props.rowData)">
                          <i class="icofont-edit"/>
                        </b-button>

                        <b-button
                          size="sm"
                          :disabled="checkPermItem('assign_user_delete')"
                          variant="danger default btn-square"
                          @click="promptDeleteUserRoles(props.rowData.userId)">
                          <i class="icofont-bin"/>
                        </b-button>
                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="userPagination"
                    @vuetable-pagination:change-page="onUserChangePage"
                    :initial-per-page="userVuetableItems.perPage"
                    @onUpdatePerPage="userVuetableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus === 'create'" class="h-100">
          <b-col cols="12" class="form-section">
            <b-row class="h-100">
              <b-col cols="5">
                <b-row>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select
                        v-model="userForm.orgId"
                        :options="orgNameSelectData" plain
                        :state="!$v.userForm.orgId.$dirty ? null : !$v.userForm.orgId.$invalid"
                        :disabled="pageStatus !== 'create'"
                      />
                      <div v-if="!$v.userForm.orgId.$invalid">&nbsp;</div>
                      <b-form-invalid-feedback>
                        {{ $t('permission-management.user.orgId-field-is-mandatory') }}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.user')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select
                        v-model="userForm.userId"
                        :options="userSelectData" plain
                        :state="!$v.userForm.userId.$dirty? null : !$v.userForm.userId.$invalid"
                        :disabled="pageStatus !== 'create'"
                      />
                      <div v-if="!$v.userForm.userId">&nbsp;</div>
                      <b-form-invalid-feedback>
                        {{ $t('permission-management.user.userId-field-is-mandatory') }}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('menu.account')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label style="height: 15px;">{{selectedUser.userAccount}}</label>
                      <div class="invalid-feedback d-block">&nbsp;</div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label class="">{{selectedUserGender}}</label>
                      <div class="invalid-feedback d-block">&nbsp;</div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">{{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                        class="text-danger">*</span></template>

                      <v-select
                        class="v-select-custom-style"
                        v-model="userForm.roles" multiple
                        :options="roleSelectData" :dir="direction"
                        :searchable="false"
                        :disabled="pageStatus === 'show'"
                      />
                      <div class="invalid-feedback d-block"></div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <div class="d-flex ">
                        <div>
                          <b-form-radio-group :disabled="pageStatus === 'show'" v-model="userForm.dataRangeCategory"
                                              stacked>
                            <b-form-radio class="pb-2" value="1000000501">
                              {{$t('permission-management.assign-permission-management.user-form.one-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000502">
                              {{$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000503">
                              {{$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000504">
                              {{$t('permission-management.assign-permission-management.user-form.all-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000505">
                              {{$t('permission-management.assign-permission-management.user-form.select-data-group')}}
                            </b-form-radio>
                          </b-form-radio-group>
                        </div>
                        <div class="align-self-end flex-grow-1 pl-5" style="">

                            <b-form-select class="mw-100"
                                           v-model="userForm.selectedDataGroupId"
                                           :options="dataGroupSelectData" plain
                                           :state="userForm.dataRangeCategory !== '1000000505' ? null : (!$v.userForm.selectedDataGroupId.$dirty ? null : !$v.userForm.selectedDataGroupId.$invalid)"
                                           :disabled="userForm.dataRangeCategory !== '1000000505'"
                            />
                            <b-form-invalid-feedback>
                              {{ $t('permission-management.user.orgId-field-is-mandatory') }}
                            </b-form-invalid-feedback>

                        </div>
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="12 " class="align-self-end text-right">
                <b-button size="sm" variant="info default" @click="onUserActionGroup('save-item')"
                          v-if="pageStatus !== 'show'"><i class="icofont-save"/> {{$t('permission-management.save')}}
                </b-button>
                <b-button size="sm" variant="danger default" @click="onUserActionGroup('delete-item')"
                          :disabled="checkPermItem('assign_user_delete')"
                          v-if="pageStatus === 'modify'"><i class="icofont-bin"/>
                  {{$t('permission-management.delete')}}
                </b-button>
                <b-button size="sm" variant="info default" @click="onUserActionGroup('show-list')"><i
                  class="icofont-long-arrow-left"/> {{$t('permission-management.return')}}
                </b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus !== 'create' && pageStatus !== 'table'" class="h-100">
          <b-col cols="12" class="form-section">
            <b-row class="h-100">
              <b-col cols="5">
                <b-row>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select
                        v-model="showForm.orgId"
                        :options="orgNameSelectData" plain
                        :disabled="true"
                      />
                      <div>&nbsp;</div>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.user')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-input
                        v-model="showForm.userName"
                        :options="userSelectData" plain
                        :disabled="true"
                      />
                      <div>&nbsp;</div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('menu.account')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label style="height: 15px;">{{showForm.userAccount}}</label>
                      <div class="invalid-feedback d-block">&nbsp;</div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="6">
                    <b-form-group class="mb-0">
                      <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label class="">{{showForm.gender}}</label>
                      <div class="invalid-feedback d-block">&nbsp;</div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">{{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                        class="text-danger">*</span></template>

                      <v-select
                        class="v-select-custom-style"
                        v-model="showForm.roles" multiple
                        :options="roleSelectData" :dir="direction"
                        :searchable="false"
                        :disabled="pageStatus === 'show'"
                      />
                      <div class="invalid-feedback d-block"></div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <div class="d-flex ">
                        <div>
                          <b-form-radio-group :disabled="pageStatus === 'show'" v-model="showForm.dataRangeCategory"
                                              stacked>
                            <b-form-radio class="pb-2" value="1000000501">
                              {{$t('permission-management.assign-permission-management.user-form.one-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000502">
                              {{$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000503">
                              {{$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000504">
                              {{$t('permission-management.assign-permission-management.user-form.all-user-data')}}
                            </b-form-radio>
                            <b-form-radio class="pb-2" value="1000000505">
                              {{$t('permission-management.assign-permission-management.user-form.select-data-group')}}
                            </b-form-radio>
                          </b-form-radio-group>
                        </div>
                        <div class="align-self-end flex-grow-1 pl-5" style="">

                          <b-form-select class="mw-100"
                                         v-model="showForm.selectedDataGroupId"
                                         :options="dataGroupSelectData" plain
                                         :state="showForm.dataRangeCategory !== '1000000505' ? null : (!$v.showForm.selectedDataGroupId.$dirty ? null : !$v.showForm.selectedDataGroupId.$invalid)"
                                         :disabled="showForm.dataRangeCategory !== '1000000505' || pageStatus === 'show'"
                          />
                          <b-form-invalid-feedback>
                            {{ $t('permission-management.user.orgId-field-is-mandatory') }}
                          </b-form-invalid-feedback>

                        </div>
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="12 " class="align-self-end text-right">
                <b-button size="sm" variant="info default" @click="onUserActionGroup('modify-item')"
                          v-if="pageStatus !== 'show'"><i class="icofont-save"/> {{$t('permission-management.save')}}
                </b-button>
                <b-button size="sm" variant="danger default" @click="onUserActionGroup('delete-item')"
                          :disabled="checkPermItem('assign_user_delete')"
                          v-if="pageStatus === 'modify'"><i class="icofont-bin"/>
                  {{$t('permission-management.delete')}}
                </b-button>
                <b-button size="sm" variant="info default" @click="onUserActionGroup('show-list')"><i
                  class="icofont-long-arrow-left"/> {{$t('permission-management.return')}}
                </b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>

      </b-tab>

      <b-tab :title="$t('permission-management.assign-permission-management.assign-to-group')"
             @click="tabStatus = 'group'">
        <b-row v-show="groupPageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.user-group')">
                      <b-form-input v-model="groupFilter.groupName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.groupMember')">
                      <b-form-input v-model="groupFilter.userName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.role')">
                      <b-form-input v-model="groupFilter.role"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.data-range')">
                      <b-form-select v-model="groupFilter.dataRange" :options="userGroupDataRangeOptions" plain/>

                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAssignUserGroupSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAssignUserGroupResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('assign_user_group_export')" @click="onExportButton()">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('assign_user_group_print')" @click="onPrintGroupButton()">
                    <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAssignUserGroupCreatePage()"
                            :disabled="checkPermItem('assign_user_group_create')" variant="success default">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>

            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="userGroupTable"
                    :api-url="userGroupTableItems.apiUrl"
                    :fields="userGroupTableItems.fields"
                    :http-fetch="userGroupTableHttpFetch"
                    pagination-path="pagination"
                    track-by="userGroupId"
                    class="table-hover"
                    :per-page="userGroupTableItems.perPage"
                    @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
                    @vuetable:pagination-data="onUserGroupTablePaginationData"

                  >
                    <template slot="groupName" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onActionGroup('show-item', props.rowData)">{{ props.rowData.groupName }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div>

                        <b-button
                          size="sm" :disabled="checkPermItem('assign_user_group_modify')"
                          variant="primary default btn-square" @click="onActionGroup('edit-item',props.rowData)">
                          <i class="icofont-edit"/>
                        </b-button>

                        <b-button
                          size="sm"
                          :disabled="checkPermItem('assign_user_group_delete')"
                          variant="danger default btn-square" @click="onActionGroup('delete-item',props.rowData)">
                          <i class="icofont-bin"/>
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="userGroupTablePagination"
                    @vuetable-pagination:change-page="onUserGroupTableChangePage"
                    :initial-per-page="userGroupTableItems.perPage"
                    @onUpdatePerPage="userGroupTableItems.perPage = Number($event)"
                  />

                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="groupPageStatus!=='table'" class="h-100">
          <b-col cols="12" class="form-section">
            <b-row class="h-100">
              <b-col cols="5">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.group.user-group')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select :disabled="groupPageStatus !== 'create'" v-model="groupForm.userGroup"
                                     :state="!$v.groupForm.userGroup.$dirty ? null : !$v.groupForm.userGroup.$invalid"
                                     :options="groupUserGroupOptions" plain/>
                      <div class="invalid-feedback d-block">
                        {{ (!$v.groupForm.userGroup.required) ?
                        $t('permission-management.assign-permission-management.group.user-group-mandatory') : "&nbsp;"
                        }}
                      </div>
                    </b-form-group>
                    <b-form-group>
                      <template slot="label">{{$t('permission-management.assign-permission-management.group.member')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label class="">{{selectedUserGroupMember?selectedUserGroupMember:" "}}</label>
                      <div class="invalid-feedback d-block">
                        {{ (submitted &&!$v.groupForm.selectedUserGroupMembers.required) ?
                        $t('permission-management.assign-permission-management.group.group-member-not-exit') : "&nbsp;"
                        }}
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                        class="text-danger">*</span></template>

                      <v-select :disabled="groupPageStatus === 'show'" class="v-select-custom-style"
                                v-model="groupForm.role" multiple :options="roleSelectData"
                                :state="!$v.groupForm.role.$dirty ? null : !$v.groupForm.role.$invalid"
                                :searchable="false" :dir="direction"/>
                      <div class="invalid-feedback d-block">
                        {{ (submitted &&!$v.groupForm.role.required) ?
                        $t('permission-management.assign-permission-management.group.role-mandatory') : "&nbsp;" }}
                      </div>

                    </b-form-group>
                    <b-form-group class="mw-100 mb-0">
                      <template slot="label">
                        {{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <div class="d-flex ">
                        <div>
                          <b-form-radio-group stacked v-model="groupForm.dataRange" :disabled="groupPageStatus === 'show'">
                            <b-form-radio value="1000000501" class="pb-2">
                              {{$t('permission-management.assign-permission-management.group.one-user-data')}}
                            </b-form-radio>
                            <b-form-radio value="1000000506" class="pb-2">
                              {{$t('permission-management.assign-permission-management.group.group-user-data')}}
                            </b-form-radio>
                            <b-form-radio value="1000000504" class="pb-2">
                              {{$t('permission-management.assign-permission-management.group.all-user-data')}}
                            </b-form-radio>
                            <b-form-radio value="1000000505" class="pb-2">
                              {{$t('permission-management.assign-permission-management.group.select-data-group')}}
                            </b-form-radio>
                          </b-form-radio-group>
                        </div>
                        <div class="align-self-end flex-grow-1 pl-5">
                          <b-form-select class="mw-100" :disabled="groupPageStatus === 'show' || groupForm.dataRange!=='1000000505'"
                                         v-model="groupForm.filterGroup"
                                         :options="dataGroupSelectData" plain/>
                        </div>
                      </div>
                      <div class="invalid-feedback d-block">
                        {{ (submitted &&!$v.groupForm.dataRange.required) ?
                        $t('permission-management.assign-permission-management.group.data-range-mandatory') : "&nbsp;"
                        }}
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="12" class="align-self-end text-right">
                <b-button v-if="groupPageStatus !== 'show'" variant="info default" size="sm"
                          @click="onActionGroup('save-item')"><i
                  class="icofont-save"/> {{$t('permission-management.save')}}
                </b-button>
                <b-button v-if="groupPageStatus === 'edit'" variant="danger default" size="sm"
                          :disabled="checkPermItem('assign_user_group_delete')"
                          @click="onActionGroup('delete-item',selectedUserGroupItem)"><i
                  class="icofont-bin"/> {{$t('permission-management.delete')}}
                </b-button>
                <b-button variant="info default" size="sm" @click="onActionGroup('show-list')"><i
                  class="icofont-long-arrow-left"/> {{$t('permission-management.return')}}
                </b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered ref="modal-user-role-delete" :title="$t('permission-management.prompt')">
      {{$t('permission-management.organization-delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deleteUserRole()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-user-role-delete')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered ref="modal-prompt-group" :title="$t('permission-management.prompt')">
      {{$t('permission-management.assign-permission-management.group.user-group-delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="fnDeleteUserGroupItem()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-prompt-group')">
          {{$t('permission-management.modal-cancel')}}
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

  import {apiBaseUrl} from '../../../constants/config';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap';
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {
    checkPermissionItem,
    getDirection,
    getLocale,
    getPermissionInfoId,
    savePermissionInfo,
    savePermissionInfoId
  } from "../../../utils";
  import {validationMixin} from 'vuelidate';
  import {downLoadFileFromServer, getApiManager, getApiManagerError, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import Modal from '../../../components/Modal/modal'

  const {required} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
      userForm: {
        orgId: {
          required
        },
        roles: {
          required
        },
        userId: {
          required
        },
        selectedDataGroupId: {
          required
        }
      },
      showForm: {
        roles: {
          required
        },
        selectedDataGroupId: {
          required
        }
      },
      groupForm: {
        userGroup: {
          required
        },
        role: {
          required,
        },
        dataRange: {
          required
        },
        selectedUserGroupMembers: {
          required
        }
      },
    },
    mounted() {

      /////////////////////////////////////////////////
      /////////// Load org data from server ///////////
      /////////////////////////////////////////////////

      let rootOrgId = 0;
      let nest = (items, rootId) =>
        items
          .filter(item => item.parentOrgId == rootId)
          .map(item => ({
            ...item,
            children: nest(items, item.orgId),
          }));

      let generateSpace = (count) => {
        let string = '';
        while (count--) {
          string += '&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        return string;
      };

      let indentData = (orgTreeData, level) => {
        orgTreeData.forEach((org) => {

          indentData(org.children, level + 1);
          this.orgNameSelectData.unshift({
              value: org.orgId,
              html: `${generateSpace(level)}${org.orgName}`
          });
        });
      };

      getApiManagerError().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
        type: 'with_parent'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgData = data;
            this.orgTreeData = nest(this.orgData, rootOrgId);

              this.orgNameSelectData = [];
            indentData(this.orgTreeData, 0);
              this.orgNameFilterData = [];
            this.orgNameSelectData.forEach(org => {
                this.orgNameFilterData.push(org);
            })
              this.orgNameFilterData.unshift({value: null, text: this.$t('permission-management.all')});
            break;
        }
      });

      ///////////////////////////////////////////////////////////
      ////////////// Load user list from server /////////////////
      ///////////////////////////////////////////////////////////

      getApiManagerError().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userList = data;
            break;
          default:

        }
      });

      ////////////////////////////////////////////////////////////
      //////////// Load role list from server ////////////////////
      ////////////////////////////////////////////////////////////

      getApiManagerError().post(`${apiBaseUrl}/permission-management/assign-permission-management/role/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.roleData = data;
            this.roleSelectData = this.roleData.map(role => ({
              label: role.roleName,
              value: role.roleId
            }));
            break;
          default:

        }
      });

      ////////////////////////////////////////////////////////////
      //////////// Load data group from the server ///////////////
      ////////////////////////////////////////////////////////////

      getApiManagerError().post(`${apiBaseUrl}/permission-management/permission-control/data-group/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.dataGroupList = data;
            this.dataGroupSelectData = this.dataGroupList.map(dataGroup => ({
              value: dataGroup.dataGroupId,
              text: dataGroup.dataGroupName
            }));
            break;
          default:

        }
      });

      ///////////////////////////////////////////////////////////
      ////////////// Load user group list from server /////////////////
      ///////////////////////////////////////////////////////////
      getApiManagerError().post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userGroupData = data;
            data.forEach(group => {
              this.groupUserGroupOptions.push(
                {text: group.groupName, value: group.userGroupId}
              )
            });
            break;
          default:
        }
      });

      this.$refs.userVuetable.$parent.transform = this.transform.bind(this);
      this.$refs.userGroupTable.$parent.transform = this.fnTransformUserGroupTable.bind(this);

    },
    data() {
      return {
        isLoading: false,
        direction: getDirection().direction,
        tabStatus: 'user',
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        renderedCheckList: [],
        renderedCheckListGroup: [],
        userFilter: {
          userName: '',
          orgId: null,
          roleName: '',
          dataRange: null
        }, // used for filtering table
        selectedUserId: null, // this is used for holding data while delete and update status modals
        userVuetableItems: { // main table options
          apiUrl: `${apiBaseUrl}/permission-management/assign-permission-management/user/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: '__slot:userName',
              title: this.$t('permission-management.assign-permission-management.user'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '9%'
            },
            {
              name: 'gender',
              title: this.$t('permission-management.gender'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '5%',
              callback: (value) => {
                const dictionary = {
                  "1000000001": `<span>${this.$t('permission-management.male')}</span>`,
                  "1000000002": `<span>${this.$t('permission-management.female')}</span>`,
                  "1000000003": `<span>${this.$t('permission-management.unknown')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'userAccount',
              title: this.$t('permission-management.th-account'),
              sortField: 'userAccount',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%'
            },
            {
              name: 'org',
              title: this.$t('permission-management.assign-permission-management.affiliated-org'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%',
              callback: (org) => {
                return org.orgName;
              }
            },
            {
              name: 'rolesLabel',
              title: this.$t('permission-management.assign-permission-management.group.role'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%',
              callback: (value) => {
                if (value === null) return '';
                if (value.isLong === false) return value.groupMember;
                else {
                  return this.hoverContent(value);
                }
              }
            },
            {
              name: 'dataRangeCategory',
              title: this.$t('permission-management.assign-permission-management.group.data-range'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '10%',
              callback: (dataRangeCategory) => {
                if (dataRangeCategory === '1000000501' || dataRangeCategory === null) {
                  return this.$t('permission-management.assign-permission-management.user-form.one-user-data');
                } else if (dataRangeCategory === '1000000502') {
                  return this.$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data');
                } else if (dataRangeCategory === '1000000503') {
                  return this.$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data');
                } else if (dataRangeCategory === '1000000504') {
                  return this.$t('permission-management.assign-permission-management.user-form.all-user-data');
                } else if (dataRangeCategory === '1000000505') {
                  return this.$t('permission-management.assign-permission-management.user-form.select-data-group');
                } else {
                  return '';
                }
              }
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-org-actions'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ],
          perPage: 10,
        },
        orgData: [], // loaded from server when the page is mounted
        orgTreeData: [],
        userList: [],
        pageStatus: 'table', // table, create, modify -> it will change the page
        roleData: [],
        dataGroupList: [],
        orgNameSelectData: [],
        orgNameFilterData: [],
        userSelectData: [],
        roleSelectData: [],
        dataGroupSelectData: [],
        rolesValid: true,
        userForm: {
          orgId: null,
          userId: null,
          nextUserId: null, // when edit or show user's role, userId should be stored here.
          roles: [],
          dataRangeCategory: "1000000501",
          selectedDataGroupId: null,
          nextSelectedDataGroupId: null, // when edit or show user's data range, dataGroupId should be stored here.
        },
        showForm: {
          orgName : null,
          orgId: null,
          userName : null,
          userId: null,
          userAccount : null,
          gender: null,
          nextUserId: null, // when edit or show user's role, userId should be stored here.
          roles: [],
          dataRangeCategory: "1000000501",
          selectedDataGroupId: null,
          nextSelectedDataGroupId: null, // when edit or show user's data range, dataGroupId should be stored here.
        },
        selectedUser: {},
        selectedUserGender: '',
        userDataRangeOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {
            value: '1000000501',
            text: this.$t('permission-management.assign-permission-management.user-form.one-user-data')
          },
          {
            value: '1000000502',
            text: this.$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data')
          },
          {
            value: '1000000503',
            text: this.$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data')
          },
          {
            value: '1000000504',
            text: this.$t('permission-management.assign-permission-management.user-form.all-user-data')
          },
          {
            value: '1000000505',
            text: this.$t('permission-management.assign-permission-management.user-form.select-data-group')
          }
        ],
        //TODO assign permission management for user group part
        userGroupDataRangeOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {
            value: '1000000501',
            text: this.$t('permission-management.assign-permission-management.group.one-user-data')
          },
          {
            value: '1000000506',
            text: this.$t('permission-management.assign-permission-management.group.group-user-data')
          },
          {
            value: '1000000504',
            text: this.$t('permission-management.assign-permission-management.group.all-user-data')
          },
          {
            value: '1000000505',
            text: this.$t('permission-management.assign-permission-management.group.select-data-group')
          }
        ],
        groupForm: {
          userGroup: null,
          role: null,
          dataRange: "1000000501",
          filterGroup: null,
          selectedUserGroupMembers: [],
        },
        selectedUserGroupItem: null,
        submitted: false,
        userGroupData: [],
        selectedUserGroupMember: "",
        groupUserGroupOptions: [],
        groupPageStatus: 'table', //table, create
        groupFilter: {
          groupName: null,
          userName: null,
          role: null,
          dataRange: null,
          filterGroup: null
        },
        userGroupTableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/assign-permission-management/user-group/get-by-filter-and-page`,
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
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: '__slot:groupName',
              title: this.$t('permission-management.assign-permission-management.group.user-group'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'groupMember',
              title: this.$t('permission-management.assign-permission-management.group.groupMember'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '25%',
              callback: (value) => {
                if (value === null) return '';
                if (value.isLong === false) return value.groupMember;
                else {
                  return this.hoverContent(value);
                }
              }
            },
            {
              name: 'groupRole',
              title: this.$t('permission-management.assign-permission-management.group.role'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '25%',
              callback: (value) => {
                if (value === null) return '';
                if (value.isLong === false) return value.groupMember;
                else {
                  return this.hoverContent(value);
                }
              }
            },
            {
              name: 'dataRangeCategory',
              title: this.$t('permission-management.assign-permission-management.group.data-range'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%',
              callback: (dataRangeCategory) => {
                if (dataRangeCategory === '1000000501') {
                  return this.$t('permission-management.assign-permission-management.user-form.one-user-data');
                } else if (dataRangeCategory === '1000000503') {
                  return this.$t('permission-management.assign-permission-management.group.group-user-data');
                } else if (dataRangeCategory === '1000000504') {
                  return this.$t('permission-management.assign-permission-management.user-form.all-user-data');
                } else if (dataRangeCategory === '1000000505') {
                  return this.$t('permission-management.assign-permission-management.user-form.select-data-group');
                } else if (dataRangeCategory === '1000000506') {
                  return this.$t('permission-management.assign-permission-management.group.group-user-data');
                } else {
                  return '';
                }
              }
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.user.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width:'10%'
            }
          ],
        }
      }
    },
    watch: {
      'userVuetableItems.perPage': function (newVal) {
        this.$refs.userVuetable.refresh();
        this.changeCheckAllStatus();
      },
      'userGroupTableItems.perPage': function (newVal) {
        this.$refs.userGroupTable.refresh();
        this.changeCheckAllStatusGroup();
      },
      'userForm.orgId': function (newVal) {
        this.userSelectData = this.userList.filter(user => user.orgId === newVal)
          .map(user => ({
            value: user.userId,
            text: user.userName,
          }));

        this.userForm.userId = this.userForm.nextUserId;
        this.userForm.nextUserId = null;
      },
      'userForm.userId': function (newVal) {
        this.selectedUser = {};
        this.userList.forEach(user => {
          if (user.userId === newVal) {
            this.selectedUser = user;
          }
        });

        if (this.selectedUser.gender === '1000000001') {
          this.selectedUserGender = this.$t('permission-management.male');
        } else if (this.selectedUser.gender === '1000000002') {
          this.selectedUserGender = this.$t('permission-management.female');
        } else if (this.selectedUser.gender === '1000000003') {
          this.selectedUserGender = this.$t('permission-management.unknown');
        } else {
          this.selectedUserGender = '';
        }
      },
      'userForm.dataRangeCategory': function (newVal) {
        this.userForm.selectedDataGroupId = this.userForm.nextSelectedDataGroupId;
        this.userForm.nextSelectedDataGroupId = null;
      },
      // 'showForm.dataRangeCategory': function (newVal) {
      //   this.showForm.selectedDataGroupId = this.showForm.nextSelectedDataGroupId;
      //   this.showForm.nextSelectedDataGroupId = null;
      // },
      'groupForm.userGroup': function (newVal, oldVal) {
        this.groupForm.selectedUserGroupMembers = null;
        if (this.userGroupData.length === 0)
          this.selectedUserGroupMember = "";
        else {
          let userGroupMembers = [];
          this.userGroupData.forEach(group => {
            if (group.userGroupId === newVal) {
              if (group.users != null && group.users.length > 0) {
                group.users.forEach(user => {
                  userGroupMembers.push(user.userName);
                });
              }
            }
          });
          this.groupForm.selectedUserGroupMembers = userGroupMembers.length > 0 ? 1 : null;
          this.selectedUserGroupMember = userGroupMembers.join(",");
        }
      }
    },
    methods: {
      hoverContent(value) {
        let content = '<div class="item-wrapper slide-right">\n' +
          '      <span class="item d-flex flex-column">\n' + value.label +
          '      </span>\n' +
          '      <div class="item-extra-info flex-column d-flex">\n' + value.groupMember +
          '      </div>\n' +
          '    </div>';
        return content;
      },
      // removeSpace(e){
      //
      // },
      selectAll(value){
        this.$refs.userVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.userVuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        this.$refs.userVuetable.isCheckAllStatus=false;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.userVuetable.selectedTo;
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
        this.$refs.userGroupTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.userGroupTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userGroupTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userGroupTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup(){
        let selectList = this.$refs.userGroupTable.selectedTo;
        let renderedList = this.renderedCheckListGroup;
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
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton() {
        if (this.tabStatus === 'user') {
          this.onExportUser();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
        this.isModalVisible = true;
      },
      onExportButtonModel() {
        if (this.tabStatus === 'user') {
          this.onExportUser();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
      },
      onExportUser() {
        let checkedAll = this.$refs.userVuetable.checkedAllStatus;
        let checkedIds = this.$refs.userVuetable.selectedTo;
        let httpOption = this.$refs.userVuetable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.userFilter,
          'idList': checkedIds.join()
        };
        this.link = `permission-management/assign-permission-management/user`;
        this.name = this.$t('permission-management.assign-permission-management.assign-to-user');
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'Assign-User', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintUserButton() {
        let checkedAll = this.$refs.userVuetable.checkedAllStatus;
        let checkedIds = this.$refs.userVuetable.selectedTo;
        let httpOption = this.$refs.userVuetable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.userFilter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/assign-permission-management/user`;
        printFileFromServer(link, params);
      },
      onExportGroup() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let httpOption = this.$refs.userGroupTable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        this.link = `permission-management/assign-permission-management/user-group`;
        this.name = this.$t('permission-management.assign-permission-management.assign-to-group');
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'Assign-UserGroup', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintGroupButton() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let httpOption = this.$refs.userGroupTable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/assign-permission-management/user-group`;
        printFileFromServer(link, params);
      },

      userVuetableFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userVuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            userName: this.userFilter.userName,
            orgId: this.userFilter.orgId,
            roleName: this.userFilter.roleName,
            dataRangeCategory: this.userFilter.dataRange
          }
        });
      },
      onUserPaginationData(paginationData) {
        this.$refs.userPagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onUserChangePage(page) {
        this.$refs.userVuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      isReload(oldPermInfoId, newPermInfoId){

        let data = [];
        newPermInfoId.forEach(item => {
          if (item.resourceId != null)
            data.push(item.resourceId);
        });


        data.sort((a,b) => a-b);
        let oldData = [];
        // oldData = oldPermInfoId;
        oldData = JSON.parse(oldPermInfoId);
        oldData.sort((a,b) => a-b);

        if(JSON.stringify(oldData) !== JSON.stringify(data)){
          window.location.reload();
        }
      },
      onUserActionGroup(value) {
        switch (value) {
          case 'modify-item':
            if (this.pageStatus === 'modify') {
              let permInfoId = getPermissionInfoId();
              getApiManager()
                .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/modify/assign-role-and-data-range`, {
                  userId: this.showForm.userId,
                  roleIdList: this.showForm.roles.map(selectedRole => selectedRole.value),
                  dataRangeCategory: this.showForm.dataRangeCategory,
                  selectedDataGroupId: this.showForm.selectedDataGroupId
                }).then((response) => {
                let message = response.data.message;
                let data = response.data.data;
                switch (message) {
                  case responseMessages['ok']:

                    savePermissionInfo(response.data.data);
                    savePermissionInfoId(response.data.data);
                    this.isReload(permInfoId, response.data.data);
                    this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-modified`), {
                      duration: 3000,
                      permanent: false
                    });
                    this.pageStatus = 'table';
                    this.$refs.userVuetable.reload();
                    break;
                  case responseMessages['exist-user']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.user-exist`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;

                  default:
                }
              });
            }
            break;
          case 'save-item':

            this.$v.userForm.$touch();
            // this.$v.groupForm.$invalid

              if(this.$v.userForm.orgId.$invalid){
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.please-select-user-organization`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              if(this.$v.userForm.userId.$invalid) {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.please-select-user`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              if(this.$v.userForm.roles.$invalid) {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.please-select-user-role`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }

              //this.rolesValid = false;

            if (!this.$v.userForm.userId.$invalid && (this.userForm.dataRangeCategory !== '1000000505' || !this.$v.userForm.selectedDataGroupId.$invalid)) {
              this.isLoading = true;
              if (this.pageStatus === 'create') {
                let permInfoId = getPermissionInfoId();
                getApiManager()
                  .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/create/assign-role-and-data-range`, {
                    userId: this.userForm.userId,
                    roleIdList: this.userForm.roles.map(selectedRole => selectedRole.value),
                    dataRangeCategory: this.userForm.dataRangeCategory,
                    selectedDataGroupId: this.userForm.selectedDataGroupId
                  }).then((response) => {
                  let message = response.data.message;
                  let data = response.data.data;
                  switch (message) {
                    case responseMessages['ok']:
                      savePermissionInfo(response.data.data);
                      savePermissionInfoId(response.data.data);
                      this.isReload(permInfoId, response.data.data);

                      this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-created`), {
                        duration: 3000,
                        permanent: false
                      });
                      this.initializeUserForm();
                      this.pageStatus = 'table';
                      this.$refs.userVuetable.reload();

                      break;
                    case responseMessages['exist-user']:
                      this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.user-exist`), {
                        duration: 3000,
                        permanent: false
                      });
                      break;
                    default:
                  }
                  this.isLoading = false;
                });
              }
              if (this.pageStatus === 'modify') {
                let permInfoId = getPermissionInfoId();
                getApiManager()
                  .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/modify/assign-role-and-data-range`, {
                    userId: this.userForm.userId,
                    roleIdList: this.userForm.roles.map(selectedRole => selectedRole.value),
                    dataRangeCategory: this.userForm.dataRangeCategory,
                    selectedDataGroupId: this.userForm.selectedDataGroupId
                  }).then((response) => {
                  let message = response.data.message;
                  let data = response.data.data;
                  switch (message) {
                    case responseMessages['ok']:

                      savePermissionInfo(response.data.data);
                      savePermissionInfoId(response.data.data);
                      this.isReload(permInfoId, response.data.data);
                      this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-modified`), {
                        duration: 3000,
                        permanent: false
                      });
                      this.pageStatus = 'table';
                      this.$refs.userVuetable.reload();
                      break;
                    case responseMessages['exist-user']:
                      this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.user-exist`), {
                        duration: 3000,
                        permanent: false
                      });
                      break;

                    default:
                  }
                  this.isLoading = false;
                });
              }
            }
            break;
          case 'show-list':
            this.pageStatus = 'table';
            break;
          case 'delete-item':
            this.promptDeleteUserRoles(this.showForm.userId);
            break;
        }
      },

      onUserNameClicked(userWithRole) {
        this.showForm.orgId = userWithRole.org.orgId;
        this.showForm.orgName = userWithRole.org.orgName;
        this.showForm.userId = userWithRole.userId;
        this.showForm.userName = userWithRole.userName;
        this.showForm.userAccount = userWithRole.userAccount;
        if (userWithRole.gender === '1000000001') {
          this.showForm.gender = this.$t('permission-management.male');
        } else if (userWithRole.gender === '1000000002') {
          this.showForm.gender = this.$t('permission-management.female');
        } else if (userWithRole.gender === '1000000003') {
          this.showForm.gender = this.$t('permission-management.unknown');
        } else {
          this.showForm.gender = '';
        }
        this.showForm.roles = userWithRole.roles.map(role => ({
          label: role.roleName,
          value: role.roleId
        }));
        if (userWithRole.dataRangeCategory == null) {
          this.showForm.dataRangeCategory = '1000000501';
        }
        else {
          this.showForm.dataRangeCategory = userWithRole.dataRangeCategory;
          if (this.showForm.dataRangeCategory === '1000000505') {
            if (userWithRole.dataGroups !== undefined && userWithRole.dataGroups !== null)
              this.showForm.selectedDataGroupId = userWithRole.dataGroups[0].dataGroupId;
          }
        }

        this.pageStatus = 'show';
        // this.userForm.orgId = userWithRole.org.orgId;
        // this.userForm.nextUserId = userWithRole.userId;
        // this.userForm.roles = userWithRole.roles.map(role => ({
        //   label: role.roleName,
        //   value: role.roleId
        // }));
        // if (userWithRole.dataRangeCategory == null) userWithRole.dataRangeCategory = '1000000501';
        // this.userForm.dataRangeCategory = userWithRole.dataRangeCategory;
        // if (this.userForm.dataRangeCategory === '1000000505' && userWithRole.dataGroups.length > 0) {
        //   this.userForm.nextSelectedDataGroupId = userWithRole.dataGroups[0].dataGroupId;
        // }
        // this.pageStatus = 'show';
      },

      promptDeleteUserRoles(userId) {
        this.selectedUserId = userId;
        this.$refs['modal-user-role-delete'].show();
      },

      deleteUserRole() {
        this.hideModal('modal-user-role-delete');

        if (this.selectedUserId) {
          let permInfoId = getPermissionInfoId();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/modify/assign-role-and-data-range`, {
              userId: this.selectedUserId,
              roleIdList: [],
              dataRangeCategory: '1000000501',
              selectedDataGroupId: null
            }).then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:

                savePermissionInfo(response.data.data);
                savePermissionInfoId(response.data.data);
                this.isReload(permInfoId, response.data.data);
                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-deleted`), {
                  duration: 3000,
                  permanent: false
                });
                this.selectedUserId = null;
                this.pageStatus = 'table';
                this.$refs.userVuetable.refresh();
                break;
              default:
            }
          });

        }
      },

      editUserRoles(userWithRole) {

        this.showForm.orgId = userWithRole.org.orgId;
        this.showForm.orgName = userWithRole.org.orgName;
        this.showForm.userId = userWithRole.userId;
        this.showForm.userName = userWithRole.userName;
        this.showForm.userAccount = userWithRole.userAccount;
        if (userWithRole.gender === '1000000001') {
          this.showForm.gender = this.$t('permission-management.male');
        } else if (userWithRole.gender === '1000000002') {
          this.showForm.gender = this.$t('permission-management.female');
        } else if (userWithRole.gender === '1000000003') {
          this.showForm.gender = this.$t('permission-management.unknown');
        } else {
          this.showForm.gender = '';
        }
        this.showForm.roles = userWithRole.roles.map(role => ({
          label: role.roleName,
          value: role.roleId
        }));
        if (userWithRole.dataRangeCategory == null) {
          this.showForm.dataRangeCategory = '1000000501';
        }
        else {
          this.showForm.dataRangeCategory = userWithRole.dataRangeCategory;
          if (this.showForm.dataRangeCategory === '1000000505') {
            if (userWithRole.dataGroups !== undefined && userWithRole.dataGroups !== null)
              this.showForm.selectedDataGroupId = userWithRole.dataGroups[0].dataGroupId;
          }
        }

        // this.userForm.orgId = userWithRole.org.orgId;
        // this.userForm.nextUserId = userWithRole.userId;
        // this.userForm.roles = userWithRole.roles.map(role => ({
        //   label: role.roleName,
        //   value: role.roleId
        // }));
        // if (userWithRole.dataRangeCategory == null) {
        //   userWithRole.dataRangeCategory = '1000000501';
        // }
        // else if (this.userForm.dataRangeCategory === '1000000505') {
        //   if(userWithRole.dataGroups !== null) {
        //     this.userForm.nextSelectedDataGroupId = userWithRole.dataGroups[0].dataGroupId;
        //   }
        // }
        // else {
        //   this.userForm.dataRangeCategory = userWithRole.dataRangeCategory;
        // }
        this.pageStatus = 'modify';
      },

      //TODO assign user group point
      onActionGroup(value, data = null) {
        this.selectedUserGroupItem = data;
        switch (value) {
          case 'show-list':
            this.groupPageStatus = 'table';
            break;
          case 'save-item':
            this.fnAssignUserGroupItem();
            break;
          case 'show-item':
            this.groupPageStatus = 'show';
            this.fnShowUserGroupItem(data);
            break;
          case 'edit-item':
            this.groupPageStatus = 'edit';
            this.fnShowUserGroupItem(data);
            break;
          case 'delete-item':
            this.fnShowUserGroupConfDiaglog(data);
            break;
        }
      },
      onAssignUserGroupSearchButton() {
        this.$refs.userGroupTable.refresh();
      },
      onAssignUserGroupResetButton() {
        this.groupFilter = {
          groupName: null,
          userName: null,
          role: null,
          dataRange: null,
          filterGroup: null
        };
      },

      onAssignUserCreatePage() {
        this.initializeUserForm();
        this.pageStatus = 'create';
        this.$v.userForm.$reset();
      },

      onAssignUserGroupCreatePage() {
        this.groupForm = {
          userGroup: null,
          role: null,
          dataRange: "1000000501",
          filterGroup: null,
          selectedUserGroupMembers: [],
        };
        this.groupPageStatus = 'create';
      },
      userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckListGroup =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userGroupTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            groupName: this.groupFilter.groupName,
            userName: this.groupFilter.userName,
            roleName: this.groupFilter.role,
            dataRangeCategory: this.groupFilter.dataRange
          }
        });
      },
      onUserGroupTablePaginationData(paginationData) {
        this.$refs.userGroupTablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatusGroup();
      },

      onUserGroupTableChangePage(page) {
        this.$refs.userGroupTable.changePage(page);
        this.changeCheckAllStatusGroup();
      },
      fnShowUserGroupItem(userGroupItem) {
        this.selectedUserGroupItem = userGroupItem;
        this.groupForm.userGroup = userGroupItem.userGroupId;
        this.groupForm.dataRange = userGroupItem.dataRangeCategory;
        this.groupForm.filterGroup = userGroupItem.dataGroups.length === 0 ? null : userGroupItem.dataGroups[0].dataGroupId;
        this.selectedUserGroupMember = "";
        this.groupForm.selectedUserGroupMembers = [];
        this.groupForm.role = [];
        userGroupItem.users.forEach(user => {
          this.groupForm.selectedUserGroupMembers.push(user.userName)
        });
        this.selectedUserGroupMember = this.groupForm.selectedUserGroupMembers.join(',');
        userGroupItem.roles.forEach(role => {
          this.groupForm.role.push({
            label: role.roleName, value: role.roleId
          })
        });
      },
      fnShowUserGroupConfDiaglog(userGroupItem) {
        this.selectedUserGroupItem = userGroupItem;
        this.$refs['modal-prompt-group'].show();
      },
      fnDeleteUserGroupItem() {
        if (this.selectedUserGroupItem && this.selectedUserGroupItem.userGroupId > 0) {
          this.$refs['modal-prompt-group'].hide();
          let permInfoId = getPermissionInfoId();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/modify/assign-role-and-data-range`, {
              userGroupId: this.selectedUserGroupItem.userGroupId,
              dataRangeCategory: "1000000501",
              selectedDataGroupId: 0,
              roleIdList: []
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  savePermissionInfo(response.data.data);
                  savePermissionInfoId(response.data.data);
                  this.isReload(permInfoId, response.data.data);
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.assign-permission-management.group.group-assigned-removed-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.groupPageStatus = 'table';
                  this.$refs.userGroupTable.refresh();
                  this.selectedUserGroupItem = null;
                  break;
              }
            })
            .catch((error) => {
            })
            .finally(() => {
              this.groupPageStatus = 'table';

            });
        } else
          console.log('this not selected');

      },

      fnAssignUserGroupItem() {

        this.submitted = true;
        this.$v.groupForm.$touch();
        if (this.$v.groupForm.$invalid) {
          if(this.$v.groupForm.userGroup.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.assign-permission-management.group.please-enter-user-group`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.groupForm.role.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.assign-permission-management.group.please-enter-group-role`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        let dataRangeGroupID = 0;
        if (this.groupForm.dataRange === '1000000505')
          dataRangeGroupID = this.groupForm.filterGroup;
        let groupSelectedRoles = [];
        this.groupForm.role.forEach(role => {
          groupSelectedRoles.push(role.value);
        });
        this.isLoading = true;
        if(this.groupPageStatus==='create') {
          let permInfoId = getPermissionInfoId();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/create/assign-role-and-data-range`, {
              userGroupId: this.groupForm.userGroup,
              dataRangeCategory: this.groupForm.dataRange,
              selectedDataGroupId: dataRangeGroupID,
              roleIdList: groupSelectedRoles
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.assign-permission-management.group.group-assigned-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  savePermissionInfo(response.data.data);
                  savePermissionInfoId(response.data.data);
                  this.isReload(permInfoId, response.data.data);
                  this.$refs.userGroupTable.reload();
                  this.selectedUserGroupItem = null;

                  break;
                case responseMessages['exist-user-group']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.user-group-exist`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
              }
              this.isLoading = false;
            })
            .catch((error) => {
              this.isLoading = false;
            })
            .finally(() => {
              this.groupPageStatus = 'table';
            });
        }
        if(this.groupPageStatus==='edit') {
          let permInfoId = getPermissionInfoId();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/modify/assign-role-and-data-range`, {
              userGroupId: this.groupForm.userGroup,
              dataRangeCategory: this.groupForm.dataRange,
              selectedDataGroupId: dataRangeGroupID,
              roleIdList: groupSelectedRoles
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  savePermissionInfo(response.data.data);
                  savePermissionInfoId(response.data.data);
                  this.isReload(permInfoId, response.data.data);
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.assign-permission-management.group.group-assigned-successfully`), {
                    duration: 3000,
                    permanent: false
                  });

                  this.$refs.userGroupTable.reload();
                  this.selectedUserGroupItem = null;

                  break;
                case responseMessages['exist-user-group']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.user-group-exist`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
              }
              this.isLoading = false;
            })
            .catch((error) => {
              this.isLoading = false;
            })
            .finally(() => {
              this.groupPageStatus = 'table';
            });
        }

      },

      transform(response) {

        let transformed = {};

        let data = response.data;

        transformed.userPagination = {
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
          let userMembers = [];
          temp.roles.forEach(role => {
            userMembers.push(role.roleName);
          });
          let groupMember = userMembers.join(',');

          let isLong = false;
          if(groupMember.length>20){
            isLong = true;
            temp.rolesLabel = {
              groupMember : groupMember,
              label : groupMember.substr(0, 19) + '...',
              isLong : isLong
            };
          }
          else {
            temp.rolesLabel = {
              groupMember : groupMember,
              isLong : isLong
            };
          }
          transformed.data.push(temp);
          this.renderedCheckList.push(data.data[i].userId);
        }

        return transformed

      },

      fnTransformUserGroupTable(response) {
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
          this.renderedCheckListGroup.push(data.data[i].userGroupId);
          temp = data.data[i];
          let userMembers = [];
          temp.users.forEach(user => {
            userMembers.push(user.userName);
          });
          let groupRoles = [];
          temp.roles.forEach(role => {
            groupRoles.push(role.roleName);
          });
          //temp.groupRole = groupRoles.join(',');
          //temp.groupMember = userMembers.join(',');

          let groupMember = userMembers.join(',');
          // if(groupMember.length>20){
          //   groupMember = groupMember.substr(0, 20) + "···"; // Gets the first part
          // }
          // temp.groupMember =groupMember;
          let roles = groupRoles.join(',');
          // if(roles.length>20){
          //   roles = roles.substr(0, 20) + "···"; // Gets the first part
          // }
          // temp.groupRole =roles;
          let isLong = false;
          if(groupMember.length>20){
            isLong = true;
            temp.groupMember = {
              groupMember : groupMember,
              label : groupMember.substr(0, 19) + '...',
              isLong : isLong
            };
          }
          else {
            temp.groupMember = {
              groupMember : groupMember,
              isLong : isLong
            };
          }
          let isLong2 = false;
          if(roles.length>20){
            isLong2 = true;
            temp.groupRole = {
              groupMember : roles,
              label : roles.substr(0, 19) + '...',
              isLong : isLong2
            };
          }
          else {
            temp.groupRole = {
              groupMember : roles,
              isLong : isLong2
            };
          }
          transformed.data.push(temp);
        }

        return transformed

      },
      onUserTablePaginationData(paginationData) {
        this.$refs.userGroupTablePagination.setPaginationData(paginationData)
      },
      onClickUserSearchButton() {
        this.$refs.userVuetable.refresh();
      },
      onClickUserResetButton() {
        this.userFilter = {
          userName: '',
          orgId: null,
          roleName: '',
          dataRange: null
        };
      },
      initializeUserForm() {
        this.userForm = {
          orgId: null,
          userId: null,
          nextUserId: null, // when edit or show user's role, userId should be stored here.
          roles: [],
          dataRangeCategory: "1000000501",
          selectedDataGroupId: null,
          nextSelectedDataGroupId: null, // when edit or show user's data range, dataGroupId should be stored here.
        };
        this.selectedUser = {};
        this.selectedUserGender = '';
      }
    }
  }
</script>

