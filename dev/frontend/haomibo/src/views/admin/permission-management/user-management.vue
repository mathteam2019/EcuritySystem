<style lang="scss">
  .user-management {
    .img-wrapper {
      padding: 5px;
      border: solid 1px #bdbaba;
      border-radius: 3px;
      width: 190px;
      height: 270px;

      img {
        width: 100%;
        height: auto;
        object-fit: contain;
      }
    }
  }
</style>
<template>
  <div class="user-management">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('permission-management.member-table')">
        <b-row v-show="pageStatus=='table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="7">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('permission-management.username')">
                      <b-form-input v-model="filter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('permission-management.status')">
                      <b-form-select v-model="filter.status" :options="statusSelectData" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('permission-management.gender')">
                      <b-form-select v-model="filter.gender" :options="genderFilterOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('permission-management.affiliated-institution')">
                      <b-form-select v-model="filter.orgId"
                                     :options="orgNameSelectData"
                                     plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="5" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                  <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                  <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_export')" @click="onExportUserButton()">
                  <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_print')" @click="onPrintUserButton()">
                  <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                </b-button>
                <b-button size="sm" class="ml-2" @click="onCreatePage()" :disabled="checkPermItem('user_create')" variant="success default">
                  <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="userTableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    track-by="userId"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onUserTablePaginationData"
                  >
                    <template slot="userNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show', props.rowData, props.rowIndex)">{{ props.rowData.userNumber }}</span>
                    </template>
                    <template slot="actions" slot-scope="props">
                      <div>

                        <b-button
                          v-if="props.rowData.status=='1000000302'"
                          size="sm"
                          variant="primary default btn-square"
                          :disabled="checkPermItem('user_modify')"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='1000000302'"
                          size="sm"
                          variant="primary default btn-square"
                          disabled>
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='1000000302'"
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('activate', props.rowData, props.rowIndex)"
                          :disabled="checkPermItem('user_update_status')">
                          <i class="icofont-check-circled"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='1000000301'"
                          size="sm"
                          :disabled="checkPermItem('user_update_status')"
                          variant="warning default btn-square"
                          @click="onAction('inactivate', props.rowData, props.rowIndex)">
                          <i class="icofont-ban"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='1000000302' && props.rowData.status!='1000000301'"
                          size="sm"
                          variant="success default btn-square"
                          disabled>
                          <i class="icofont-ban"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='1000000302'"
                          size="sm"
                          variant="danger default btn-square"
                          :disabled="checkPermItem('user_update_status')"
                          @click="onAction('blocked', props.rowData, props.rowIndex)">
                          <i class="icofont-minus-circle"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='1000000303'"
                          size="sm"
                          variant="success default btn-square"
                          :disabled="checkPermItem('user_update_status')"
                          @click="onAction('unblock', props.rowData, props.rowIndex)">
                          <i class="icofont-power"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='1000000302' && props.rowData.status!='1000000303'"
                          size="sm"
                          variant="danger default btn-square"
                          disabled>
                          <i class="icofont-minus-circle"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='1000000304'"
                          size="sm"
                          variant="purple default btn-square"
                          :disabled="checkPermItem('user_modify')"
                          @click="onAction('reset-password', props.rowData, props.rowIndex)">
                          <i class="icofont-ui-password"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='1000000304'"
                          size="sm"
                          variant="purple default btn-square"
                          disabled>
                          <i class="icofont-ui-password"></i>
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onUserTableChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>

                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus=='create'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userName"
                                :state="!$v.profileForm.userName.$dirty ? null : !$v.profileForm.userName.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-name')"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.userName.required) ?
                    $t('permission-management.user.username-field-is-mandatory') :
                    (!$v.profileForm.userName.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                    }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userNumber"
                                :state="!$v.profileForm.userNumber.$dirty ? null : !$v.profileForm.userNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-id')"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.userNumber.required) ?
                    $t('permission-management.user.userNumber-field-is-mandatory') :
                    (!$v.profileForm.userNumber.alphaNum)
                    ?$t('permission-management.user.userNumber-should-be-numerical-or-characters'):
                    (!$v.profileForm.userNumber.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                    }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="profileForm.gender" :options="genderOptions" plain
                                 :state="!$v.profileForm.gender.$dirty ? null : !$v.profileForm.gender.$invalid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.gender.required) ?
                    $t('permission-management.user.gender-field-is-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.license-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.identityCard"
                                :state="!$v.profileForm.identityCard.$dirty ? null : !$v.profileForm.identityCard.$invalid"
                                :placeholder="$t('permission-management.please-enter-license-number')"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.identityCard.required) ?
                    $t('permission-management.user.license-number-field-is-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="profileForm.orgId" :options="orgNameSelectData" plain
                                 :state="!$v.profileForm.orgId.$dirty ? null : !$v.profileForm.orgId.$invalid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.orgId.required) ?
                    $t('permission-management.user.orgId-field-is-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.post')}}</template>
                  <b-form-input type="text" v-model="profileForm.post"
                                :placeholder="$t('permission-management.please-enter-post')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.education')}}</template>
                  <b-form-select v-model="profileForm.education" :options="educationOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.degree')}}</template>
                  <b-form-select v-model="profileForm.degree" :options="degreeOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-3">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.email')}}</template>
                  <b-form-input type="email"
                                v-model="profileForm.email"
                                :state="!$v.profileForm.email.$dirty ? null : !$v.profileForm.email.$invalid"
                                :placeholder="$t('permission-management.please-enter-email')"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.email.email) ?
                    $t('permission-management.user.email-field-should-email-format') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.phone')}}</template>
                  <b-form-input type="text" v-model="profileForm.mobile"
                                :state="!$v.profileForm.mobile.$dirty ? null : !$v.profileForm.mobile.$invalid"
                                :placeholder="'000-0000-0000'"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.address')}}</template>
                  <b-form-input type="text" v-model="profileForm.address"
                                :placeholder="$t('permission-management.please-enter-address')"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="profileForm.userAccount"
                                :state="!$v.profileForm.userAccount.$dirty ? null : !$v.profileForm.userAccount.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-account')"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.userAccount.required) ?
                    $t('permission-management.user.account-field-is-mandatory') : " " }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group style="max-width: unset">
                  <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <div class="d-flex ">
                    <div>
                      <b-form-radio-group v-model="profileForm.passwordType" stacked>
                        <b-form-radio value="default" class="pb-2">
                          {{$t('permission-management.password-basic')}}
                        </b-form-radio>
                        <b-form-radio value="other" class="pb-2">
                          {{$t('permission-management.password-other')}}
                        </b-form-radio>
                      </b-form-radio-group>
                    </div>
                    <div class="align-self-end flex-grow-1 pl-3">
                      <b-form-input type="password" v-model="profileForm.passwordValue"
                                    :disabled="profileForm.passwordType==='default'"
                                    :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group :label="$t('permission-management.note')">
                  <b-form-textarea type="text" v-model="profileForm.note"
                                   :placeholder="$t('permission-management.please-enter-note')"/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="2" class="d-flex align-items-center flex-column">
            <div class="mb-4 img-wrapper position-relative">
              <div class=" p-1">
                <img :src="profileForm.avatar" onerror="src='\\assets\\img\\profile.png'" class="card-img-top"/>
              </div>
              <div class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp.png">
              </div>
              <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
            </div>
            <b-button @click="$refs.profileFile.click()" class="mb-1" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button @click="onSaveUserPage()" variant="info default" size="sm"><i
                class="icofont-save"></i> {{
                $t('permission-management.save') }}
              </b-button>
              <b-button @click="onTableListPage()" variant="danger default" size="sm"><i
                class="icofont-long-arrow-left"></i> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus=='show'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userName"
                                :placeholder="$t('permission-management.please-enter-user-name')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userNumber"

                                :placeholder="$t('permission-management.please-enter-user-id')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="profileForm.gender" :options="genderOptions" plain
                  />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.license-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.identityCard"
                                :placeholder="$t('permission-management.please-enter-license-number')"></b-form-input>

                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="profileForm.orgId" :options="orgNameSelectData" plain
                  />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.post')}}</template>
                  <b-form-input type="text" v-model="profileForm.post"
                                :placeholder="$t('permission-management.please-enter-post')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.education')}}</template>
                  <b-form-select v-model="profileForm.education" :options="educationOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.degree')}}</template>
                  <b-form-select v-model="profileForm.degree" :options="degreeOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-3">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.email')}}</template>
                  <b-form-input type="email" v-model="profileForm.email"
                                :placeholder="$t('permission-management.please-enter-email')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.phone')}}</template>
                  <b-form-input type="text" v-model="profileForm.mobile"
                                :placeholder="$t('permission-management.please-enter-phone')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.address')}}</template>
                  <b-form-input type="text" v-model="profileForm.address"
                                :placeholder="$t('permission-management.please-enter-address')"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userAccount"
                                :placeholder="$t('permission-management.please-enter-user-account')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <div class="d-flex ">
                    <div>
                      <b-form-radio-group v-model="profileForm.passwordType" stacked>
                        <b-form-radio value="default">
                          {{$t('permission-management.password-basic')}}
                        </b-form-radio>
                        <b-form-radio value="other">
                          {{$t('permission-management.password-other')}}
                        </b-form-radio>
                      </b-form-radio-group>
                    </div>
                    <div class="align-self-end flex-grow-1 pl-2">
                      <b-form-input type="password" v-model="profileForm.passwordValue"
                                    :disabled="profileForm.passwordType==='default'"
                                    :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group :label="$t('permission-management.note')">
                  <b-form-textarea type="text" v-model="profileForm.note"
                                   :placeholder="$t('permission-management.please-enter-note')"/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="2" class="text-right">
            <div class="mb-4 img-wrapper position-relative">
              <div class=" p-1">
                <img :src="profileForm.avatar" onerror="src='\\assets\\img\\profile.png'" class="card-img-top"/>
              </div>
              <div class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp.png">
              </div>
              <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
            </div>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button v-if="profileForm.status==='1000000301'" class="mr-1" @click="onAction('inactivate', profileForm)"
                      variant="warning default" size="sm"><i class="icofont-ban"></i> {{
              $t('permission-management.action-make-inactive') }}
            </b-button>
            <b-button v-if="profileForm.status==='1000000302'" class="mr-1" @click="onAction('activate', profileForm)"
                      variant="success default" size="sm"><i class="icofont-check-circled"></i> {{
              $t('permission-management.action-unblock') }}
            </b-button>
            <b-button @click="onTableListPage()" variant="danger default" size="sm"><i
              class="icofont-long-arrow-left"></i> {{
              $t('permission-management.return') }}
            </b-button>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.user-group')">
        <b-row class="h-100 ">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="3" class="pr-3">
                  <b-form-group class="search-form-group">
                    <template slot="label">{{$t('permission-management.user.user-group-name')}}</template>
                    <b-form-input :placeholder="$t('permission-management.user.please-enter-group-name')"
                                  v-model="groupFilter.name"></b-form-input>
                  </b-form-group>
                </b-col>
                <b-col cols="9" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupSearchButton()">
                      <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupResetButton()">
                      <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_group_export')" @click="onExportGroupButton()">
                      <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_group_print')" @click="onPrintGroupButton()">
                      <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onUserGroupCreateButton()" :disabled="checkPermItem('user_group_create')" variant="success default">
                      <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>

              <b-row class="flex-grow-1 m-0">
                <b-col cols="12">
                  <div class="table-wrapper table-responsive">
                    <vuetable
                      ref="userGroupTable"
                      :api-url="userGroupTableItems.apiUrl"
                      :fields="userGroupTableItems.fields"
                      :http-fetch="userGroupTableHttpFetch"
                      pagination-path="userGroupPagination"
                      track-by="userGroupId"
                      class="table-hover"
                      @vuetable:pagination-data="onUserGroupTablePaginationData"
                    >
                      <template slot="userGroupNumber" slot-scope="props">
                        <span class="cursor-p text-primary" @click="onUserGroupTableRowClick(props.rowData)">{{ props.rowData.groupNumber }}</span>
                      </template>
                      <template slot="operating" slot-scope="props">
                        <b-button variant="danger default btn-square" class="m-0" :disabled="checkPermItem('user_group_delete')"
                                  @click="onAction('group-remove', props.rowData, props.rowIndex)"><i
                          class="icofont-bin"></i></b-button>
                      </template>
                    </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="userGroupPagination"
                      @vuetable-pagination:change-page="onUserGroupTableChangePage"
                      :initial-per-page="userGroupTableItems.perPage"
                      @onUpdatePerPage="userGroupTableItems.perPage = Number($event)"
                    ></vuetable-pagination-bootstrap>
                    <b-modal centered ref="modal-prompt-group" :title="$t('permission-management.prompt')">
                      {{$t('permission-management.user.user-group-delete-prompt')}}
                      <template slot="modal-footer">
                        <b-button variant="primary" @click="fnDeleteUserGroupItem()" class="mr-1">
                          {{$t('permission-management.modal-ok')}}
                        </b-button>
                        <b-button variant="danger" @click="fnHideModal('modal-prompt-group')">
                          {{$t('permission-management.modal-cancel')}}
                        </b-button>
                      </template>
                    </b-modal>
                  </div>
                </b-col>
              </b-row>
            </div>
          </b-col>
          <b-col cols="4" class="pl-0 " v-if="selectedUserGroupItem">
            <div class="section d-flex flex-column h-100 px-3">
              <div v-if="groupForm.status=='create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="groupForm.groupNumber"
                    :state="!$v.groupForm.groupNumber.$invalid"/>
                </b-form-group>
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-if="groupForm.status=='create'"
                                v-model="groupForm.groupName"
                                :state="!$v.groupForm.groupName.$invalid"/>
                </b-form-group>
              </div>
              <div v-if="groupForm.status!='create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{selectedUserGroupItem.groupNumber}}
                  </label>

                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{selectedUserGroupItem.groupName}}
                  </label>

                </b-form-group>
              </div>
              <div>
                <label class="font-weight-bold">{{$t('permission-management.user.group-member')}}<span
                  class="text-danger">*</span></label>
              </div>
              <div class="text-left">
                <b-form-group>
                  <b-form-checkbox v-model="isSelectedAllUsersForDataGroup">
                    {{$t('permission-management.permission-control.select-all')}}
                  </b-form-checkbox>
                </b-form-group>
              </div>
              <div class="flex-grow-1 overflow-auto" style="height: 0;">

                <div>
                  <v-tree ref='orgUserTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true'/>
                </div>
              </div>
              <div class="d-flex align-items-end justify-content-end pt-3" v-if="groupForm.status=='create'">
                <div>
                  <b-button @click="onClickCreateUserGroup" variant="info default" size="sm"><i
                    class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                </div>
              </div>
              <div class="d-flex align-items-end justify-content-end pt-3" v-if="groupForm.status!='create'">
                <div>
                  <b-button @click="onClickModifyUserGroup" variant="info default" :disabled="checkPermItem('user_group_modify')" size="sm"><i
                    class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="onClickDeleteUserGroup"  :disabled="checkPermItem('user_group_delete')" variant="danger default" size="sm"><i
                    class="icofont-bin"></i> {{$t('permission-management.delete')}}
                  </b-button>

                </div>
              </div>
            </div>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered ref="modal-prompt" :title="$t('permission-management.prompt')">
      {{promptTemp.action==='blocked'?$t('permission-management.user.block-prompt'):$t('permission-management.user.inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="fnChangeItemStatus()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="fnHideModal('modal-prompt')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>

  </div>

</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {checkPermissionItem,getDirection} from "../../../utils";
  import {downLoadFileFromServer, getApiManager, isPhoneValid, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {validationMixin} from 'vuelidate';
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree



  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  /**
   * getting orgFull name with parent org
   * @param orgData
   * @returns {*}
   */
  let fnGetOrgFullName = orgData => {
    let orgFullName = '';
    if (orgData == null)
      return orgFullName;
    while (orgData.parent != null) {
      orgFullName += '/' + orgData.orgName;
      orgData = orgData.parent;
    }
    orgFullName = orgData.orgName + orgFullName;

    return orgFullName;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree
    },
    mixins: [validationMixin],
    validations: {
      profileForm: {
        userName: {
          required, maxLength: maxLength(50)
        },
        userNumber: {
          required, alphaNum, maxLength: maxLength(50)
        },
        gender: {
          required,
        },
        identityCard: {
          required
        },
        orgId: {
          required
        },
        email: {
          email
        },
        userAccount: {
          required, maxLength: maxLength(50)
        },
        mobile: {
          isPhoneValid,
        }
      },
      groupForm: {
        groupName: {
          required
        },
        groupNumber: {
          required
        }
      }
    },
    mounted() {

      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
      this.$refs.userGroupTable.$parent.transform = this.fnTransformUserGroupTable.bind(this);
      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
        type: 'with_parent'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgData = data;
            break;
        }
      });
      getApiManager().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {
        type: 'with_org_tree'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userData = data;
            break;
        }
      })

    },
    data() {
      return {
        isLoading: false,
        submitted: false,
        tableData: [],
        pageStatus: 'table',
        defaultOrgId: '',
        filter: {
          userName: '',
          status: null,
          orgId: '',
          gender: null
        },
        promptTemp: {
          userId: 0,
          action: ''
        },
        orgData: [],
        userData: [],
        orgUserTreeData: [],
        direction: getDirection().direction,
        genderOptions: [
          {value: '1000000001', text: this.$t('permission-management.male')},
          {value: '1000000002', text: this.$t('permission-management.female')},
        ],
        genderFilterOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000001', text: this.$t('permission-management.male')},
          {value: '1000000002', text: this.$t('permission-management.female')},
        ],
        statusSelectData: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000301', text: this.$t('permission-management.active')},
          {value: '1000000302', text: this.$t('permission-management.inactive')},
          {value: '1000000304', text: this.$t('permission-management.pending')},
          {value: '1000000303', text: this.$t('permission-management.blocked')},
        ],
        orgNameSelectData: {},
        educationOptions: [
          {value: '1000000101', text: this.$t('permission-management.belowcollege')},
          {value: '1000000102', text: this.$t('permission-management.student')},
          {value: '1000000103', text: this.$t('permission-management.master_student')},
          {value: '1000000104', text: this.$t('permission-management.doctor_student')},
          {value: '1000000105', text: this.$t('permission-management.other')},
        ],
        degreeOptions: [
          {value: '1000000201', text: this.$t('permission-management.belowcollege')},
          {value: '1000000202', text: this.$t('permission-management.bachelor')},
          {value: '1000000203', text: this.$t('permission-management.master')},
          {value: '1000000204', text: this.$t('permission-management.doctor')},
          {value: '1000000205', text: this.$t('permission-management.other')},
        ],

        profileForm: {
          status: '1000000102',
          userId: 0,
          avatar: '',
          userName: '',
          userNumber: '',
          gender: '',
          identityCard: '',
          orgId: '',
          post: '',
          education: '',
          degree: '',
          email: '',
          mobile: '',
          address: '',
          userAccount: '',
          passwordType: 'default',
          passwordValue: '',
          note: '',
          portrait: null
        },
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: 'userId',
              title: this.$t('permission-management.th-no'),
              sortField: 'userId',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '6%'
            },
            {
              name: '__slot:userNumber',
              title: this.$t('permission-management.th-user-id'),
              sortField: 'userNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '12%'
            },
            {
              name: 'userName',
              title: this.$t('permission-management.th-username'),
              sortField: 'userName',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '12%'
            },
            {
              name: 'gender',
              title: this.$t('permission-management.gender'),
              sortField: 'gender',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000001": `<span>${this.$t('permission-management.male')}</span>`,
                  "1000000002": `<span>${this.$t('permission-management.female')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              },
              width: '11%'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-status'),
              sortField: 'status',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000301": `<span class="text-success">${this.$t('permission-management.active')}</span>`,
                  "1000000302": `<span class="text-muted">${this.$t('permission-management.inactive')}</span>`,
                  "1000000303": `<span class="text-danger">${this.$t('permission-management.blocked')}</span>`,
                  "1000000304": `<span class="text-warning">${this.$t('permission-management.pending')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              },
              width: '11%',
            },
            {
              name: 'orgName',
              title: this.$t('permission-management.th-affiliated-institution'),
              sortField: 'orgName',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'userAccount',
              title: this.$t('permission-management.th-account'),
              sortField: 'userAccount',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '13%'
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-action'),
              titleClass: 'text-center',
              dataClass: 'text-center btn-actions',
              width: '250px'
            },

          ],
          perPage: 10,
        },
        //second tab content
        selectedUserGroupItem: null,
        groupForm: {
          groupName: '',
          groupNumber: '',
          status: 'create'
        },
        groupFilter: {
          name: ''
        },
        userGroupTableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user-group/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: 'userGroupId',
              title: this.$t('permission-management.th-no'),
              sortField: 'userGroupId',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:userGroupNumber',
              title: this.$t('permission-management.user.user-group-number'),
              sortField: 'groupNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'groupName',
              title: this.$t('permission-management.user.user-group-name'),
              sortField: 'userGroupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.user.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            }
          ],
        },

        isSelectedAllUsersForDataGroup: false,
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      'userGroupTableItems.perPage': function (newVal) {
        this.$refs.userGroupTable.refresh();
      },
      orgData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let nest = (items, id = 0) =>
          items
            .filter(item => item.parentOrgId == id)
            .map(item => ({
              ...item,
              children: nest(items, item.orgId),
              id: id++,
              label: `${item.orgNumber} ${item.orgName}`
            }));

        this.treeData = nest(newVal)[0];
        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].orgId == org.parentOrgId) {
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

        let selectOptions = [];

        newVal.forEach((org) => {
          selectOptions.push({
            value: org.orgId,
            html: `${generateSpace(getLevel(org))}${org.orgName}`
          });
        });

        this.orgNameSelectData = selectOptions;

        this.filter.orgId = this.treeData.orgId;
        this.defaultOrgId = this.treeData.orgId;
        this.fnRefreshOrgUserTreeData();
      },
      userData(newVal) {
        this.fnRefreshOrgUserTreeData();
      },
      selectedUserGroupItem(newVal) {
        if (newVal) {
          let userGroupList = [];
          newVal.users.forEach((user) => {
            userGroupList.push(user.userId);
          });
          this.userData.forEach((user) => {
            user.selected = userGroupList.includes(user.userId);
          });
          this.fnRefreshOrgUserTreeData();
        }
      },
      isSelectedAllUsersForDataGroup(newVal) {

        if (this.selectedUserGroupItem) {
          let tempSelectedUserGroup = this.selectedUserGroupItem;
          tempSelectedUserGroup.users = newVal ? this.userData : [];
          this.selectedUserGroupItem = null;
          this.selectedUserGroupItem = tempSelectedUserGroup;
        }
      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportUserButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user`;
        downLoadFileFromServer(link, params, 'user');
      },
      onPrintUserButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user`;
        printFileFromServer(link, params);
      },
      onExportGroupButton() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user-group`;
        downLoadFileFromServer(link, params, 'userGroup');
      },
      onPrintGroupButton() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user-group`;
        printFileFromServer(link, params);
      },

      onCreatePage() { // move to create page
        // reset models
        this.onInitialUserData();
        this.submitted = false;
        // reset create form
        this.$v.profileForm.$reset();
        // change page to create
        this.pageStatus = 'create';
      },
      onTableListPage() {
        this.pageStatus = 'table';
      },
      onSaveUserPage() {
        this.submitted = true;
        this.$v.profileForm.$touch();
        if (this.$v.profileForm.$invalid) {
          return;
        }
        const formData = new FormData();
        for (let key in this.profileForm) {
          if (key !== 'portrait')
            formData.append(key, this.profileForm[key]);
          else if (this.profileForm['portrait'] !== null)
            formData.append(key, this.profileForm[key], this.profileForm[key].name);
        }
        this.isLoading = true;
        // call api
        let finalLink = this.profileForm.userId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user-management/user/` + finalLink, formData)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.profileForm.userId > 0 ? this.$t(`permission-management.user-modify-successfully`) : this.$t(`permission-management.user-created-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.onInitialUserData();
                // back to table
                this.pageStatus = 'table';
                this.$refs.vuetable.reload();

                break;
              case responseMessages['used-user-account']://duplicated user account
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user-account-already-used`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-email']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.used-email`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-mobile']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.used-phone`), {
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
      onAction(action, data, index) {
        let userId = data.userId;
        switch (action) {
          case 'modify':
            this.fnModifyItem(data);
            break;
          case 'show':
            this.fnShowItem(data);
            break;
          case 'reset-password':
          case 'activate':
          case 'unblock':
            this.fnChangeItemStatus(userId, action);
            break;
          case 'inactivate':
          case 'blocked':
            this.fnShowConfDiaglog(userId, action);
            break;
          case 'group-remove':
            this.fnShowUserGroupConfDiaglog(data);
            break;
        }
      },
      fnHideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
        this.promptTemp = {
          userId: 0,
          action: ''
        }
      },
      fnShowConfDiaglog(userId, action) {
        this.promptTemp.userId = userId;
        this.promptTemp.action = action;
        this.$refs['modal-prompt'].show();
      },
      fnModifyItem(data) {
        this.onInitialUserData();
        for (let key in this.profileForm) {
          if (Object.keys(data).includes(key)) {
            if (key !== 'portrait' && key !== 'avatar')
              this.profileForm[key] = data[key];
            else if (key === 'portrait')
              this.profileForm.avatar = apiBaseUrl + data['portrait'];
          }
        }
        this.profileForm.portrait = null;
        this.profileForm.passwordType = 'default';
        this.pageStatus = 'create';
      },
      fnShowItem(data) {
        this.onInitialUserData();
        for (let key in this.profileForm) {
          if (Object.keys(data).includes(key))
            if (key !== 'portrait' && key !== 'avatar')
              this.profileForm[key] = data[key];
            else if (key === 'portrait')
              this.profileForm.avatar = apiBaseUrl + data['portrait'];
        }
        this.profileForm.portrait = null;
        this.profileForm.passwordType = 'default';
        this.pageStatus = 'show';
      },
      fnChangeItemStatus(userId = 0, action = '') {
        if (userId === 0)
          userId = this.promptTemp.userId;
        if (action === '')
          action = this.promptTemp.action;
        let status = action;
        if (status === 'unblock' || status === 'reset-password' || status === 'inactivate')
          status = '1000000302';
        else if(status === 'activate')
          status = '1000000301';
        else if(status === 'blocked')
          status = '1000000303';
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user-management/user/update-status`, {
            'userId': userId,
            'status': status,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user-change-status-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.profileForm.status = status;
                this.$refs.vuetable.reload();

                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-prompt'].hide();
          });

      },
      onFileChange(e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length)
          return;
        this.onCreateImage(files[0]);
      },
      onCreateImage(file) {
        this.profileForm.avatar = new Image();
        let reader = new FileReader();
        reader.onload = (e) => {
          this.profileForm.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
        this.profileForm.portrait = file;
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          userName: '',
          status: null,
          orgId: '',
          category: null
        };
        if (this.defaultOrgId !== '')
          this.filter.orgId = this.defaultOrgId;
      },
      onInitialUserData() {
        this.profileForm = {
          status: '1000000102',
          userId: 0,
          avatar: '',
          userName: '',
          userNumber: '',
          gender: '',
          identityCard: '',
          orgId: '',
          post: '',
          education: '',
          degree: '',
          email: '',
          mobile: '',
          address: '',
          userAccount: '',
          passwordType: 'default',
          passwordValue: '',
          note: '',
          portrait: null
        }
      },
      transform(response) {

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
          temp.orgName = temp.org.orgName;
          transformed.data.push(temp);
        }

        return transformed

      },

      //second tab content
      fnShowUserGroupConfDiaglog(userGroupItem) {
        this.selectedUserGroupItem = userGroupItem;
        this.$refs['modal-prompt-group'].show();
      },
      fnDeleteUserGroupItem() {
        if (this.selectedUserGroupItem && this.selectedUserGroupItem.userGroupId > 0) {
          this.$refs['modal-prompt-group'].hide();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/delete`, {
              userGroupId: this.selectedUserGroupItem.userGroupId
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.group-removed-successfully`), {
                    duration: 3000,
                    permanent: false
                  });

                  this.$refs.userGroupTable.refresh();
                  this.selectedUserGroupItem = null;
                  break;
                case responseMessages['has-users']: // okay
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-users`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;

              }
            })
            .catch((error) => {
            })
            .finally(() => {

            });
        }
      },
      fnTransformUserGroupTable(response) {
        this.selectedUserGroupItem = null;
        let transformed = {};

        let data = response.data;

        transformed.userGroupPagination = {
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
          transformed.data.push(temp)
        }

        return transformed

      },
      userTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            userName: this.filter.userName,
            status: this.filter.status,
            orgId: this.filter.orgId,
            gender: this.filter.gender,
          }
        });
      },
      onUserTablePaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onUserTableChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userGroupTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            groupName: this.groupFilter.name,
          }
        });
      },
      onUserGroupTablePaginationData(paginationData) {
        this.$refs.userGroupPagination.setPaginationData(paginationData)
      },
      onUserGroupTableChangePage(page) {
        this.$refs.userGroupTable.changePage(page)
      },
      onDataGroupRowClass(dataItem, index) {
        let selectedItem = this.selectedUserGroupItem;
        if (selectedItem && selectedItem.userGroupId === dataItem.userGroupId) {
          return 'selected-row';
        } else {
          return '';
        }
      },
      onUserGroupTableRowClick(dataItems) {
        this.selectedUserGroupItem = dataItems;
        this.groupForm.status = 'modify';
      },
      // user tree group
      fnRefreshOrgUserTreeData() {
        let pseudoRootId = 0;
        let nest = (orgData, userData, rootId = pseudoRootId) => {
          let childrenOrgList = orgData
            .filter(org => org.parentOrgId === rootId)
            .map(org => ({
              ...org,
              title: org.orgName,
              expanded: true,
              children: nest(orgData, userData, org.orgId)
            }));
          let childrenUserList = userData
            .filter(user => user.orgId === rootId)
            .map(user => ({
              ...user,
              isUser: true,
              title: user.userName,
              expanded: true,
              checked: user.selected,
              children: []
            }));
          return [...childrenOrgList, ...childrenUserList];
        };
        this.orgUserTreeData = nest(this.orgData, this.userData, pseudoRootId);
      },
      onUserGroupSearchButton() {
        this.$refs.userGroupTable.refresh();
      },
      onUserGroupResetButton() {
        this.groupFilter = {
          name: null
        };
      },
      onUserGroupCreateButton() {
        this.selectedUserGroupItem = {
          users: []
        };
        this.groupForm = {
          groupNumber: null,
          groupName: null,
          status: 'create'
        }
      },
      onClickDeleteUserGroup() {
        this.fnShowUserGroupConfDiaglog(this.selectedUserGroupItem);
      },
      onClickCreateUserGroup() {
        if (this.selectedUserGroupItem) {
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userGroupUserIds = [];
          checkedNodes.forEach((node) => {
            if (node.isUser) userGroupUserIds.push(node.userId);
          });
          if (userGroupUserIds.length == 0) {
            return;
          }
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, {
              'groupName': this.groupForm.groupName,
              'groupNumber': this.groupForm.groupNumber,
              'userIdList': userGroupUserIds
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.user-group-modified-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.userGroupTable.refresh();
                  this.groupForm = {
                    groupName: null,
                    groupNumber: null,
                    status: 'create'
                  };
                  break;
                case responseMessages['used-user-group-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-user-group-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {

            }).finally(() => {
            //

          });
        }
      },
      onClickModifyUserGroup() {
        if (this.selectedUserGroupItem) {
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userGroupUserIds = [];
          checkedNodes.forEach((node) => {
            if (node.isUser) userGroupUserIds.push(node.userId);
          });
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/modify`, {
              'userGroupId': this.selectedUserGroupItem.userGroupId,
              'userIdList': userGroupUserIds
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.user-group-modified-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.userGroupTable.reload();
                  break;
                case responseMessages['used-user-group-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-user-group-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {

            });
        }
      }
    }
  }
</script>
