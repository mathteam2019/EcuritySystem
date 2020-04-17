<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab" v-show="pageStatus === 'table'" style="margin-top: 20px;">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input v-model="filter.taskNumber"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.operation-mode')">
                  <b-form-select v-model="filter.mode" :options="operationModeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input v-model="filter.userName"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.on-site')">
                  <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
                </b-form-group>
              </b-col>

              <div class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
              </div>
            </b-row>
          </b-col>
          <b-col cols="8" v-if="isExpanded">
            <b-row>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.start-time')">
                  <date-picker v-model="filter.startTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.end-time')">
                  <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
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
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default"
                        :disabled="checkPermItem('history_task_export')" @click="onExportButton()">
                <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export')}}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default"
                        :disabled="checkPermItem('history_task_print')" @click="onPrintButton()">
                <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="taskVuetable"
                track-by="historyId"
                :api-url="taskVuetableItems.apiUrl"
                :fields="taskVuetableItems.fields"
                :http-fetch="taskVuetableHttpFetch"
                :per-page="taskVuetableItems.perPage"
                pagination-path="pagination"
                class="table-hover"
                @vuetable:checkbox-toggled="onCheckStatusChange"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="task" slot-scope="props">
                    <span class="cursor-p text-primary"
                          @click="onRowClicked(props.rowData.historyId)">
                      {{props.rowData.task.taskNumber}}
                    </span>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="props.rowData.workMode==null"></div>

                  <div v-else>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('all')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan+judge')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan+hand')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                  </div>
                </template>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="taskVuetablePagination"
                :initial-per-page="taskVuetableItems.perPage"
                @vuetable-pagination:change-page="onTaskVuetableChangePage"
                @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <div v-show="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col class="col-30" style="padding-left: 13px; padding-right: 8px">
          <b-card class="h-100">
            <div
              style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 5px;"/>
            <div>
              <div style="font-size: 15px; font-weight: bold; margin-bottom: 1.5rem;">
                {{$t('personal-inspection.scanned-image')}}
              </div>
            </div>
            <b-row class="mb-1">
              <b-col style="margin-bottom: 6px;" class="icon-container">
                <div v-if="showPage.workMode==null"></div>

                <div v-else>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('all')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan+judge')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan+hand')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                </div>
              </b-col>
              <b-col style="margin-bottom: 5px;" class="text-right icon-container">
                <span v-if="showPage.serKnowledgeCase!=null && showPage.serKnowledgeCase.caseId !=null"><i
                  class="icofont-star"/></span>
                <span v-if="showPage.judgeUser != null && showPage.judgeUser.userId != defaultUserId"><i
                  class="icofont-search-user"/></span>
                <span v-else>
                  <b-img src="/assets/img/system_scan.svg" style="width: 20px; height: 22px;"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000002'"><i
                  class="icofont-female"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000001'"><i
                  class="icofont-male" style="color: darkblue;"/></span>
              </b-col>
            </b-row>
            <b-row style="margin-bottom: 0.5rem;">
              <b-col style="padding-right: 0.5rem; padding-left: 1rem;">
                <canvas id="firstcanvas" style="height: 24vw;" class="img-fluid w-100 "/>
              </b-col>
              <b-col style="padding-right: 1rem; padding-left: 0.5rem;">
                <canvas id="secondcanvas" style="height: 24vw;" class="img-fluid w-100 "/>
                <div style="width: 100%; height: 24px;" class="text-right icon-container">
                  <div v-if="power===true">
                    <b-img :disabled="power===true" src="/assets/img/previous_cartoon.png" class="operation-icon"
                           @click="previousImage()"/>
                    <b-img src="/assets/img/next_cartoon.png" class="operation-icon" @click="nextImage()"/>
                  </div>
                </div>
              </b-col>
            </b-row>
            <b-row style="float: right;">
              <b-col class="control-group">
                <div v-if="power===true" class="control-btn-wrapper">
                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png" @click="onlyOneSlide(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png" @click="onlyOneSlide(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png" @click="filterId(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png" @click="filterId(3)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png" @click="filterId(4)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png" @click="filterId(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png" @click="filterId(12)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(10)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(9)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(11)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png" @click="filterId(13)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>
                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png" @click="loadImage()"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>
                <div v-else style="opacity: 0.5" class="control-btn-wrapper">
                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>
                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>
                <div class="switch-wrapper">
                  <div v-if="power===true" class="separator"></div>
                  <div v-else style="opacity: 0.5" class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" :disabled="checkPermItem('history_task_toggle')" theme="custom"
                              color="info"/>
                  </div>
                </div>
              </b-col>
            </b-row>
            <b-row style="height: 15px !important;">
              <b-col v-if="isSlidebar2Expended" style="max-width: 100%; flex: none;">
                <vue-slider
                  v-model="slidebar2value"
                  :min="-50"
                  :max="50"
                  :dot-options="dotOptions"
                  :order="false"
                />
              </b-col>
              <b-col v-if="isSlidebar1Expended" style="max-width: 100%; flex: none;">
                <vue-slider
                  v-model="slidebar1value"
                  :min="-50"
                  :max="50"
                  :dot-options="dotOptions"
                  :order="false"
                />
              </b-col>
            </b-row>
          </b-card>
        </b-col>
        <b-col class="col-70" style="padding-right: 13px">
          <b-card class="h-100 d-flex flex-column right-card">
            <div style="height: 20px;">
              <div
                style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
              <div style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.history')}}</div>
            </div>
            <div class="history-chart">
              <div>
                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.start')}}</div>
                  </div>

                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.scan')}}</div>
                    <div>
                      <div v-if="showPage.scanPointsmanName != null">{{showPage.scanPointsmanName}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label
                      v-if="showPage.scanStartTime != null">{{this.getDateTimeFormat2(showPage.scanStartTime)}}</label>
                    <label v-else/>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.scanEndTime != null">{{this.getDateTimeFormat2(showPage.scanEndTime)}}</label>
                    <label v-else/>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.judge')}}</div>
                    <div>
                      <div v-if="showPage.judgeUser != null">{{showPage.judgeUser.userName}}</div>
                      <div v-else>{{$t('maintenance-management.process-task.default-user')}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label v-if="showPage.judgeStartTime==null"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.judgeStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.judgeEndTime==null"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.judgeEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('device-config.maintenance-config.inspection')}}</div>
                    <div>
                      <div v-if="showPage.handUser == null"></div>
                      <div v-else>{{showPage.handUser.userName}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label v-if="showPage.workMode==null || showPage.handUserId === null || showPage.handStartTime ===null"/>
                    <label v-else-if="showPage.workMode.modeName===getModeDataCode('scan+judge') || showPage.workMode.modeName===getModeDataCode('scan')"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.handStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.workMode==null || showPage.handUserId === null || showPage.handStartTime ===null"/>
                    <label v-else-if="showPage.workMode.modeName===getModeDataCode('scan+judge') || showPage.workMode.modeName===getModeDataCode('scan')"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.handEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.end')}}</div>
                  </div>

                </div>

              </div>
            </div>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.task == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                v-model="showPage.task.taskNumber"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-if="showPage.task==null"
                                class="form-input-border"/>
                  <b-form-input disabled v-else-if="showPage.task.field==null"
                                class="form-input-border"/>
                  <b-form-input disabled v-else v-model="showPage.task.field.fieldDesignation"
                                class="form-input-border"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-if="showPage.scanDevice == null" class="form-input-border"/>
                  <b-form-input disabled v-else v-model="showPage.scanDevice.deviceName"
                                class="form-input-border"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.serScan == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getOptionValue(showPage.serScan.scanImageGender)"/>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.handDevice == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                v-model="showPage.handDevice.deviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.judgeDevice == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                v-model="showPage.judgeDevice.deviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.workMode==null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getModeName(showPage.workMode.modeName)"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}
                    <span class="text-danger">*</span>
                  </template>
<!--                  <b-form-input disabled class="form-input-border"-->
<!--                                v-if="conclusionType == null"-->
<!--                                :value="$t('maintenance-management.process-task.system')"/>-->
<!--                  <b-form-input disabled class="form-input-border" v-else-->
<!--                                :value="getOptionValue(conclusionType)"/>-->
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.judgeUser != null && showPage.judgeUser.userId != defaultUserId"
                                :value="$t('maintenance-management.process-task.artificial')"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="$t('maintenance-management.process-task.system')"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                :value="getOptionValue(showPage.handAppraise) + ' ' + getOptionValue(showPage.handAppraiseSecond)"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.history-offline')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.serScan == null || showPage.serScan.scanOffLine==null" :value="getOptionValue(0)"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getOptionValue(showPage.serScan.scanOffLine)"/>
                </b-form-group>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                     {{$t('permission-management.note')}}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border" style="max-width: 100%;"
                                v-if="showPage.note == null"/>
                  <b-form-input disabled class="form-input-border" style="max-width: 100%;" v-else
                                v-model="showPage.note"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <div
                  style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label
                  style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="justify-content-start" style="margin-bottom: 2rem; margin-top: 1rem">
                  <b-col>
                    <div v-if="handGoodExpanded[0]" class="text-center"
                         style="background-color: #ff0000; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[0]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[1]" class="text-center"
                         style="background-color: #ff4e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[1]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[2]" class="text-center"
                         style="background-color: #ff7e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[2]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[3]" class="text-center"
                         style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[3]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[4]" class="text-center"
                         style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[4]].text}}</span>
                    </div>
                  </b-col>
                </b-row>

                <div
                  style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label
                  style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery" style="margin-top: 0.5rem">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`"
                         @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 60px; height: 45px;" :alt="thumb.name"/>

                  </b-col>
                  <b-col cols="auto" v-for="(video, videoIndex) in videos" :key="`video_${videoIndex}`"
                         @click="onVideoClick(video)">
                    <video style=" width: 60px; height: 50px;">
                      <source :src="video.src" type="video/mp4">
                    </video>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()"/>
                </b-row>
              </b-col>
              <b-col style="max-width: 45%;">
                <b-row>
                  <b-col v-if="getLocale()==='zh' || getLocale() === null" cols="12" class="align-self-end text-right mt-3"
                         style="width: 100%; height: 130px !important;">
                    <div v-if="showPage.handResult !== null">
                      <b-img v-if="showPage.handResult === 'TRUE'" src="/assets/img/icon_invalid.png"
                             class="align-self-end img-result"/>
                      <b-img v-if="showPage.handResult === 'FALSE'" src="/assets/img/icon_valid.png"
                             class="align-self-end img-result"/>
                    </div>
                    <div v-else-if="showPage.judgeResult!==null">
                      <b-img v-if="showPage.judgeResult === 'TRUE'" src="/assets/img/icon_invalid.png"
                             class="align-self-end img-result"/>
                      <b-img v-if="showPage.judgeResult === 'FALSE'" src="/assets/img/icon_valid.png"
                             class="align-self-end img-result"/>
                    </div>
                    <div v-else>
                      <b-img src="/assets/img/icon_valid.png"
                             class="align-self-end img-result"/>
                    </div>
                  </b-col>
                  <b-col v-if="getLocale()==='en'" cols="12" class="align-self-end text-right mt-3">
                    <div v-if="showPage.handResult !== null">
                      <b-img v-if="showPage.handResult === 'TRUE'" src="/assets/img/icon_invalid_en.png"
                             class="align-self-end img-result"/>
                      <b-img v-if="showPage.handResult === 'FALSE'" src="/assets/img/icon_valid_en.png"
                             class="align-self-end img-result"/>
                    </div>
                    <div v-else-if="showPage.judgeResult!==null">
                      <b-img v-if="showPage.judgeResult === 'TRUE'" src="/assets/img/icon_invalid_en.png"
                             class="align-self-end img-result"/>
                      <b-img v-if="showPage.judgeResult === 'FALSE'" src="/assets/img/icon_valid_en.png"
                             class="align-self-end img-result"/>
                    </div>
                    <div v-else>
                      <b-img src="/assets/img/icon_valid.png"
                             class="align-self-end img-result"/>
                    </div>
                  </b-col>
                </b-row>
                <b-row style="margin-top: 1rem">
                  <b-col cols="12" class="align-self-end text-right mt-3">
                    <b-button size="sm" variant="orange default"
                              :disabled="checkPermItem('history_task_save')||(showPage.serKnowledgeCase!=null && showPage.serKnowledgeCase.caseId!=null)"
                              @click="showCollectionView()">
                      <i class="icofont-gift"/>
                      {{ $t('personal-inspection.collection') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onRowClicked(history_id)">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.refresh') }}
                    </b-button>
                    <b-button size="sm" variant="info default" @click="pageStatus='table'">
                      <i class="icofont-long-arrow-left"/>
                      {{ $t('personal-inspection.return') }}
                    </b-button>
                  </b-col>
                </b-row>

              </b-col>
            </b-row>
          </b-card>
        </b-col>
      </b-row>
      <div class="video-wrapper" v-show="showVideo">
        <div class="video-container">
          <video-player ref="videoPlayer" :options="videoOptions"/>
        </div>
        <span class="switch-action" @click="finishVideoShow()"><i class="icofont-close-line"/></span>
      </div>
      <b-modal centered id="model-collection" ref="model-collection">
        <template slot="modal-header">
          <h2 id="modal-reset___BV_modal_title_" style="font-size: 1.7rem; font-weight: bold;" class="modal-title">
            {{$t('system-setting.prompt')}}</h2>
          <button type="button" aria-label="Close" @click="hideModal('model-collection')" class="close">×</button>
        </template>
        <b-row style="height : 100px;">
          <b-col style="margin-top: 1rem; margin-left: 5rem; margin-right: 5rem;">
            <b-form-group class="mw-100 w-100" label="标签">
              <v-select v-model="collectionLabel" :options="collectionLabelOptions"
                        class="v-select" multiple :searchable="false" :dir="direction"/>
            </b-form-group>
          </b-col>
        </b-row>
        <div slot="modal-footer">
          <b-button size="sm" variant="orange default" @click="onCollectionClicked(history_id)">
            <i class="icofont-gift"/>
            {{ $t('personal-inspection.collection') }}
          </b-button>
          <b-button size="sm" variant="light default" @click="hideModal('model-collection')">
            <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
          </b-button>
        </div>
      </b-modal>
    </div>
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
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name" :imgLink="imgUrl"
      @close="closeModal"
    />
    <b-modal centered id="modal-image" ref="modal-image" :title="$t('system-setting.prompt')">
      {{$t('device-management.device-list.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="downLoadImage" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-image')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>
<style lang="scss">
  .vs__selected-options {
    margin-left: 1px;
    margin-bottom: 1px;
  }
  .col-30 {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 30%;
    flex: 0 0 30%;
    max-width: 30%;
  }

  .col-form-label {
    margin-bottom: 1px;
  }

  .col-70 {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 70%;
    flex: 0 0 70%;
    max-width: 70%;
  }

  .form-group-margin {
    margin-bottom: 1.5rem;
  }

  .form-input-border {
    background-color: white !important;
    border: 1px solid #ebebeb;
  }

  .img-result {
    width: 130px;
    height: 130px;
  }

  .vc-chrome {
    position: absolute;
    top: calc(2rem + 4px);
    right: 0;
    z-index: 9;
  }

  .current-color {
    display: inline-block;
    width: 1rem;
    height: 1rem;
    background-color: #000;
    cursor: pointer;
  }

  span.cursor-p {
    cursor: pointer !important;
  }

  .slide-class {
    margin-top: -30px;
  }

  .rounded-span {
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }

  .operation-icon {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }

  .icon-container {
    font-size: 20px;

    .icofont-star {
      color: #ffe400;
    }

    .icofont-search-user {
      color: #ff9c0e;
    }

    .icofont-female {
      color: #fe687f;
    }
  }

  .control-group {
    display: flex;
    align-items: flex-start;

    .control-btn-wrapper {
      display: flex;
      flex-grow: 1;
      flex-wrap: wrap;

      .control-btn {
        width: calc(100% / 6);
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 5px;

        img {
          $size: 35px;
          width: $size;
          height: $size;
          margin-bottom: 6px;
        }

        span {
          display: block;
        }
      }
    }

    .switch-wrapper {
      width: 60px;
      height: 40px;
      display: flex;
      align-items: center;

      .separator {
        border: 0;
        width: 1px;
        height: 30px;
        background: #1e9dd2;
        flex-shrink: 0;
      }

      .switch {
        .vue-switcher {
          display: flex;
          height: 100%;
          margin: 0;
          transform: scale(0.8);
        }
      }
    }

    @media screen and (max-width: 1700px) {

      .control-btn-wrapper {
        .control-btn {
          img {
            $size: 28px;
            width: $size !important;
            height: $size !important;
          }
        }
      }
      .switch-wrapper {
        height: 28px;

        .separator {
          height: 28px;
        }
      }

    }
  }

  .history-chart {

    $ratio: 12.8;

    width: 100%;
    padding-bottom: 100% / $ratio;
    position: relative;

    margin-bottom: 24px;

    & > :first-child {
      left: 0;
      height: 100%;
      position: absolute;
      top: 0;
      width: 100%;

      background: url("/assets/img/history_chart.png") no-repeat;
      background-size: contain;

      $elements: 5;
      @for $i from 0 to $elements {
        .part:nth-child(#{$i + 1}) {
          position: absolute;
          top: 25%;
          bottom: 25%;
          left: 2% + 20% * $i;
          width: 20% - 4%;
          display: flex;
          color: white;
          align-items: center;
          justify-content: space-between;

          $date-color: #0c70ab;

          .top-date {
            color: $date-color;
            position: absolute;
            top: 104%;
            left: -6%;
          }

          .bottom-date {
            color: $date-color;
            position: absolute;
            bottom: 104%;
            right: 2%;
          }
        }
      }
    }
  }

  .evidence-gallery {
    .col-auto {
      padding-right: 0px;
    }
  }

  .seized-list {
    label {
      position: absolute;
      top: -26px;
      right: 291px;
    }

    .row {
      padding-bottom: 34px;

      .col-auto {
        padding-left: 0px;
        margin-left: -8px;
      }

      .seized-item {
        width: 100px;
        height: 62px;
        border-radius: 5px;

        .contraband-icon {
          position: absolute;
          top: 4px;
          right: 20px;
          width: 25px;
          height: 18px;
        }

        span.contraband-count {
          position: center;
          top: 13px;
          left: 32px;
          color: white;
          font-size: 1.5rem;
        }

        span.contraband-category {
          position: absolute;
          bottom: 1px;
          left: 4px;
          color: white
        }
      }
    }
  }

</style>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select';
  import {
    getApiManager,
    getDateTimeWithFormat,
    downLoadFileFromServer,
    printFileFromServer,
    downLoadImageFromUrl
  } from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import 'vue-tree-halower/dist/halower-tree.min.css'
  import Switches from 'vue-switches';
  import {LightGallery} from 'vue-light-gallery';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {loadImageCanvas, getLoginInfo, imageFilterById, getLocale, getDirection} from '../../../utils'
  import VueSlideBar from 'vue-slide-bar'
  import {checkPermissionItem} from "../../../utils";
  import Videoplayer from '../../../components/Common/VideoPlayer';
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'
  import VueSlider from 'vue-slider-component'
  import 'vue-slider-component/theme/default.css'

  import {validationMixin} from 'vuelidate';

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'light-gallery': LightGallery,
      'date-picker': DatePicker,
      'video-player': Videoplayer,
      VueSlideBar,
      VueSlider,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },
    mounted() {
      this.getSiteOption();
    },
    data() {
      return {
        value1: 0,
        value2: [0, 0],
        timer: '',
        dotOptions: [{
          disabled: true
        }, {
          disabled: false
        }],
        link: '',
        params: {},
        name: '',
        imgUrl: [],
        isExport:false,
        isExpanded: false,
        pageStatus: 'table',
        power: false,
        siteData: [],
        showPage: [],
        renderedCheckList:[],

        fileSelection: [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,

        isSlidebar1Expended: false,
        isSlidebar2Expended: false,
        slidebar1value: [0, 0],
        slidebar2value: [0, 0],

        slider: {
          lineHeight: 10,
          processStyle: {
            backgroundColor: 'blue'
          }
        },

        validIcon: null,

        task_id: null,
        defaultUserId: 10000,

        apiBaseURL: '',

        thumbs: [],
        images: [],
        videos: [],
        photoIndex: null,
        showVideo: false,

        widthRate: [],
        heightRate: [],
        imgRect: [],
        cartoonRect: [],
        rRects: [],
        rectAdd: [],
        rectDel: [],
        imageUrls: [],

        imagesInfo: [],
        cartoonsInfo: [],
        imageRectR: [],
        imageRectL: [],
        cartoonRectR: [],
        cartoonRectL: [],
        cntCartoon: 0,
        orderCartoon: 0,
        mode: null,
        conclusionType: null,

        videoOptions: {
          autoplay: true,
          language: 'zh',//todo need to set that lang setting with multiple.
          poster: '/assets/img/glock-thumb.jpg', //todo need to set its image data differently if needed
          sources: [{
            type: "video/mp4",
            src: '/assets/img/113.mp4',
          }],
        },
        selectedVideo: null,

        history_id: 0,
        collectionLabel: [],
        collectionLabelOptions: [
          {value: '1000002701', label: '好'},
          {value: '1000002702', label: '良'},
          {value: '1000002703', label: '差'},
          {value: '1000002704', label: '非常差'}
        ],
        collectionLabelOption: [],

        isCheckAll: false,
        filter: {
          taskNumber: null,
          mode: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
          // TODO: search filter
        },

        taskDetailForm: {
          taskNumber: null,
          fieldName: null,
          deviceName: null,
          gender: "",
          handDeviceName: 0,
          judgeDeviceName: null,
          workModeName: null,
          handTaskResult: null,
          handAppraise: null,
          note: null,
        },

        detailForm: {},
        judgeStartTime: null,
        judgeDeviceName: null,
        judgeUserName: null,
        judgeUserId: null,
        handStartTime: null,
        handDeviceName: null,
        handUserName: null,

        imageUrl: null,
        cartoonUrl: null,
        handGoodDataCode: ['1000001601', '1000001602', '1000001603', '1000001604', '1000001605'],
        handGoodExpanded: [false, false, false, false, false],
        handGoodDataCodeExpanded: [],
        handGoodDataCodeValue: {
          1000001601: {text: '安眠药'},
          1000001602: {text: '仿真枪'},
          1000001603: {text: '玩具枪'},
          1000001604: {text: '气枪'},
          1000001605: {text: '打火机'},
        },
        // TODO: select options

        onSiteOption: [],

        httpOption: null,
        apiUrl: null,

        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1', text: this.$t('personal-inspection.security-instrument')},
          {value: '2', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
          {value: '3', text: this.$t('personal-inspection.security-instrument-and-manual-test')},
          {value: '4', text: this.$t('personal-inspection.security-instrument-and-hand-test-and-device')},
        ],
        statusOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001102', text: this.$t('maintenance-management.process-task.dispatch')},
          {value: '1000001103', text: this.$t('maintenance-management.process-task.judge')},
          {value: '1000001104', text: this.$t('maintenance-management.process-task.hand')},
          {value: '1000001106', text: this.$t('maintenance-management.process-task.scan')}
        ],

        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/history-task/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('personal-inspection.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (historyId) => {
                if (historyId == null) return '';
                return historyId;
              }
            },
            {
              name: '__slot:task',
              sortField: 'taskNumber',
              title: this.$t('personal-inspection.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:mode',
              title: this.$t('personal-inspection.operation-mode'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'handTaskResult',
              title: this.$t('personal-inspection.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handTaskResult) => {

                const dictionary = {

                  "TRUE": `<span style="color:#ef6e69;">${this.$t('knowledge-base.seized')}</span>`,
                  "FALSE": `<span style="color:#e8a23e;">${this.$t('knowledge-base.no-seized')}</span>`,

                };

                if (handTaskResult == null) return '';
                if (!dictionary.hasOwnProperty(handTaskResult)) return '';
                return dictionary[handTaskResult];
              }
            },
            {
              name: 'task',
              title: this.$t('personal-inspection.on-site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (task) => {
                if (task == null) return '';
                if (task.field == null) return '';
                return task.field.fieldDesignation;
              }
            },
            {
              name: 'scanDevice',
              title: this.$t('personal-inspection.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanDevice) => {
                if (scanDevice == null) return '';
                return scanDevice.deviceName;
              }
            },
            {
              name: 'scanPointsman',
              title: this.$t('personal-inspection.guide'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanPointsman) => {
                if (scanPointsman == null) return '';
                return scanPointsman.userName;
              }
            },
            {
              name: 'scanStartTime',
              sortField: 'scanStartTime',
              title: this.$t('personal-inspection.scan-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStartTime) => {
                if (scanStartTime == null) return '';
                return getDateTimeWithFormat(scanStartTime);
              }
            },
            {
              name: 'scanEndTime',
              sortField: 'scanEndTime',
              title: this.$t('personal-inspection.scan-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanEndTime) => {
                if (scanEndTime == null) return '';
                return getDateTimeWithFormat(scanEndTime);
              }
            },
          ],
          perPage: 10,
        },
      }
    },

    created() {
      //this.timer = setInterval(this.autoUpdate, 20000)
    },
    beforeDestroy() {
      clearInterval(this.timer)
    },

    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
        this.changeCheckAllStatus();
      },

      pageStatus(newval) {
        if (newval === 'show') {
          //clearInterval(this.timer);
        } else {
          //this.timer = setInterval(() => this.autoUpdate(), 20000);
        }
      },

      siteData: function (newVal, oldVal) {
          this.onSiteOption = [];
          let nest = (newVal, id = 0, depth = 1) =>
              newVal
                  .filter(item => item.parentFieldId == id)
                  .map(item => ({
                      data: {fieldId: item.fieldId},
                      children: nest(newVal, item.fieldId, depth + 1),
                      text: item.fieldDesignation
                  }));
          let treeData = nest(newVal);

          let generateSpace = (count) => {
              let string = '';
              while (count--) {
                  string += '&nbsp;&nbsp;&nbsp;&nbsp;';
              }
              return string;
          };

          let changeFieldTree = (treeData, index) => {
              if (!treeData || treeData.length === 0) {
                  return;
              }
              let tmp = treeData;
              for (let i = 0; i < tmp.length; i++) {
                  changeFieldTree(tmp[i].children, index + 1);
                  this.onSiteOption.unshift({
                      value: tmp[i].data.fieldId,
                      html: `${generateSpace(index)}${tmp[i].text}`
                  });
              }
          };

          changeFieldTree(treeData, 1);
          this.onSiteOption.unshift({
              value: null,
              html: `${this.$t('permission-management.all')}`
          });
      },

      power(newValue) {

        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.loadImage();

      },

      slidebar1value(newsValue, oldValue) {

        if (oldValue[1] < newsValue[1]) {
          for (let i = oldValue[1]; i < newsValue[1]; i++) {
            this.filterId(5);
          }
        } else {
          for (let i = newsValue[1]; i < oldValue[1]; i++) {
            this.filterId(6);
          }
        }
      },
      slidebar2value(newsValue, oldValue) {

        if (oldValue[1] < newsValue[1]) {
          for (let i = oldValue[1]; i < newsValue[1]; i++) {
            this.filterId(7);
          }
        } else {
          for (let i = newsValue[1]; i < oldValue[1]; i++) {
            this.filterId(8);
          }
        }
      },
    },

    methods: {
      cancelAutoUpdate() {
        clearInterval(this.timer)
      },
      selectAll(value){
        this.$refs.taskVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.taskVuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        this.$refs.taskVuetable.isCheckAllStatus=false;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.taskVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.taskVuetable.selectedTo;
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

      onVideoClick(video) {
        this.videoOptions.sources.src = video.src;
        this.$refs.videoPlayer.initialize();
        this.showVideo = true;
      },

      finishVideoShow() {
        this.showVideo = false;
        this.$refs.videoPlayer.dispose();
      },

      onThumbClick(index) {
        this.photoIndex = index;
        this.isOpen = true;
      },

      handleHide() {
        this.photoIndex = null;
        this.isOpen = false;
      },

      onlyOneSlide(value) {
        if (this.power === true) {
          if (value === 1) {
            this.isSlidebar1Expended = !this.isSlidebar1Expended;
            this.isSlidebar2Expended = !this.isSlidebar1Expended;
          }
          if (value === 2) {
            this.isSlidebar2Expended = !this.isSlidebar2Expended;
            this.isSlidebar1Expended = !this.isSlidebar2Expended;
          }
        }
      },

      filterId(id) {
        if (id < 5 || id > 8) {
          this.isSlidebar1Expended = false;
          this.isSlidebar2Expended = false;
        }
        if (this.power === true) {
          imageFilterById(id, this.cartoonRectL, this.cartoonRectR);
        }
      },

      loadImage() {
        let url1 = '';
        let url2 = '';
        this.slidebar1value = [0, 0];
        this.slidebar2value = [0, 0];
        if (this.power === false) {

          if (this.imagesInfo[0] !== undefined) {
            url1 = this.imagesInfo[0].imageUrl;
          }
          if (this.imagesInfo[1] !== undefined) {
            url2 = this.imagesInfo[1].imageUrl;
          }
          loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

        } else {
          let k = this.orderCartoon * 2;
          this.cartoonRectL = [];
          this.cartoonRectR = [];

          if (this.cartoonsInfo[k] !== undefined) {
            url1 = this.cartoonsInfo[k].imageUrl;
            if(this.cartoonsInfo[k].imageRect!=null) {
              if(this.cartoonsInfo[k].rectsDel != null){
                for (let i = 0; i < this.cartoonsInfo[k].imageRect.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k].rectsDel.length; j++) {
                    if(this.cartoonsInfo[k].imageRect[i].x === this.cartoonsInfo[k].rectsDel[j].x && this.cartoonsInfo[k].imageRect[i].y === this.cartoonsInfo[k].rectsDel[j].y && this.cartoonsInfo[k].imageRect[i].width === this.cartoonsInfo[k].rectsDel[j].width && this.cartoonsInfo[k].imageRect[i].height === this.cartoonsInfo[k].rectsDel[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonRectL.push({
                      x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].x,
                      y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].y,
                      width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].width,
                      height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].height,
                      colour: this.cartoonsInfo[k].colorRect,
                    });
                  }
                }
                for (let i = 0; i < this.cartoonsInfo[k].rectsDel.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k].imageRect.length; j++) {
                    if(this.cartoonsInfo[k].rectsDel[i].x === this.cartoonsInfo[k].imageRect[j].x && this.cartoonsInfo[k].rectsDel[i].y === this.cartoonsInfo[k].imageRect[j].y && this.cartoonsInfo[k].rectsDel[i].width === this.cartoonsInfo[k].imageRect[j].width && this.cartoonsInfo[k].rectsDel[i].height === this.cartoonsInfo[k].imageRect[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonsInfo[k].rectsDel[i].x = 0;
                    this.cartoonsInfo[k].rectsDel[i].y = 0;
                    this.cartoonsInfo[k].rectsDel[i].width = 0;
                    this.cartoonsInfo[k].rectsDel[i].height = 0;
                  }
                }
              }
              else {
                for (let i = 0; i < this.cartoonsInfo[k].imageRect.length; i++) {
                  this.cartoonRectL.push({
                    x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].x,
                    y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].y,
                    width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].width,
                    height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].height,
                    colour: this.cartoonsInfo[k].colorRect,
                  });
                }
              }
            }
            if (this.cartoonsInfo[k].rectsAdd != null) {
            for (let i = 0; i < this.cartoonsInfo[k].rectsAdd.length; i++) {

                this.cartoonRectL.push({
                  x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].x,
                  y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].y,
                  width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].width,
                  height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].height,
                  colour: this.cartoonsInfo[k].colorAdd,
                });
              }
            }

            if (this.cartoonsInfo[k].displayDel === '1000000601') {
              if (this.cartoonsInfo[k].rectsDel != null) {
                for (let i = 0; i < this.cartoonsInfo[k].rectsDel.length; i++) {
                  this.cartoonRectL.push({
                    x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].x,
                    y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].y,
                    width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].width,
                    height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].height,
                    colour: this.cartoonsInfo[k].colorDel,
                  });
                }
              }
            }
          }

          if (this.cartoonsInfo[k + 1] !== undefined) {
            url2 = this.cartoonsInfo[k + 1].imageUrl;
            if (this.cartoonsInfo[k + 1].imageRect != null) {
              if(this.cartoonsInfo[k + 1].rectsDel != null){
                for (let i = 0; i < this.cartoonsInfo[k+ 1].imageRect.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k+ 1].rectsDel.length; j++) {
                    if(this.cartoonsInfo[k+ 1].imageRect[i].x === this.cartoonsInfo[k+ 1].rectsDel[j].x && this.cartoonsInfo[k+ 1].imageRect[i].y === this.cartoonsInfo[k+ 1].rectsDel[j].y && this.cartoonsInfo[k+ 1].imageRect[i].width === this.cartoonsInfo[k+ 1].rectsDel[j].width && this.cartoonsInfo[k+ 1].imageRect[i].height === this.cartoonsInfo[k+ 1].rectsDel[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonRectR.push({
                      x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].x,
                      y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].y,
                      width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].width,
                      height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].height,
                      colour: this.cartoonsInfo[k + 1].colorRect,
                    });
                  }
                }
                for (let i = 0; i < this.cartoonsInfo[k + 1].rectsDel.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k + 1].imageRect.length; j++) {
                    if(this.cartoonsInfo[k + 1].rectsDel[i].x === this.cartoonsInfo[k + 1].imageRect[j].x && this.cartoonsInfo[k + 1].rectsDel[i].y === this.cartoonsInfo[k + 1].imageRect[j].y && this.cartoonsInfo[k + 1].rectsDel[i].width === this.cartoonsInfo[k + 1].imageRect[j].width && this.cartoonsInfo[k + 1].rectsDel[i].height === this.cartoonsInfo[k + 1].imageRect[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonsInfo[k + 1].rectsDel[i].x = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].y = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].width = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].height = 0;
                  }
                }
              }
              else {
                for (let i = 0; i < this.cartoonsInfo[k + 1].imageRect.length; i++) {
                  this.cartoonRectR.push({
                    x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].x,
                    y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].y,
                    width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].width,
                    height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].height,
                    colour: this.cartoonsInfo[k + 1].colorRect,
                  });
                }
              }
            }

            if (this.cartoonsInfo[k + 1].rectsAdd != null) {
              for (let i = 0; i < this.cartoonsInfo[k + 1].rectsAdd.length; i++) {
                this.cartoonRectR.push({
                  x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].x,
                  y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].y,
                  width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].width,
                  height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].height,
                  colour: this.cartoonsInfo[k + 1].colorAdd,
                });
              }
            }

            if (this.cartoonsInfo[k + 1].displayDel === '1000000601' && this.cartoonsInfo[k + 1].rectsDel != null) {
              for (let i = 0; i < this.cartoonsInfo[k + 1].rectsDel.length; i++) {
                this.cartoonRectR.push({
                  x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].x,
                  y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].y,
                  width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].width,
                  height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].height,
                  colour: this.cartoonsInfo[k + 1].colorDel,
                });
              }
            }
          }

          loadImageCanvas(url1, url2, this.cartoonRectL, this.cartoonRectR, this.power);

        }

      },

      previousImage() {
        if (this.orderCartoon > 0) {
          this.orderCartoon = this.orderCartoon - 1;
          this.loadImage();
        }
      },

      nextImage() {
        if (this.orderCartoon < this.cntCartoon - 1) {
          this.orderCartoon = this.orderCartoon + 1;
          this.loadImage();
        }
      },

      getOptionValue(dataCode) {
        const dictionary = {
          "1000000001": `${this.$t('permission-management.male')}`,
          "1000000002": `${this.$t('permission-management.female')}`,
          "1000000601": `${this.$t('system-setting.parameter-setting.yes')}`,
          "1000000602": `${this.$t('system-setting.parameter-setting.no')}`,
          "1000001701": `${this.$t('permission-management.timeout')}`,
          "1000001702": `${this.$t('permission-management.timein')}`,
          "TRUE": `${this.$t('knowledge-base.suspect')}`,
          "FALSE": `${this.$t('knowledge-base.no-suspect')}`,
          "1000001301": `${this.$t('permission-management.female')}`,
          "1000001302": `${this.$t('permission-management.female')}`,
          "1000001303": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001304": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001102": `${this.$t('maintenance-management.process-task.dispatch')}`,
          "1000001103": `${this.$t('maintenance-management.process-task.judge')}}`,
          "1000001104": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001106": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001201": `${this.$t('maintenance-management.process-task.system')}`,
          "1000001202": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000002301": `${this.$t('maintenance-management.process-task.system')}`,
          "1000002302": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000002303": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000001801": `${this.$t('maintenance-management.process-task.underreport')}`,
          "1000001802": `${this.$t('maintenance-management.process-task.falsepositive')}`,
          "1000001803": `${this.$t('maintenance-management.process-task.underreportfalsepositive')}`,
          "0": `${this.$t('maintenance-management.process-task.online')}`,
          "1": `${this.$t('maintenance-management.process-task.offline')}`
        };
        if (!dictionary.hasOwnProperty(dataCode)) return '';
        return dictionary[dataCode];
      },

      getModeDataCode(value) {
        const dictionary = {

          "scan": `1000001301`,
          "scan+hand": `1000001302`,
          "scan+judge": `1000001303`,
          "all": `1000001304`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getModeName(value) {
        const dictionary = {

          "1000001301": this.$t('personal-inspection.security-instrument'),
          "1000001302": this.$t('personal-inspection.security-instrument-and-hand-test'),
          "1000001303": this.$t('personal-inspection.security-instrument-and-manual-test'),
          "1000001304": this.$t('personal-inspection.security-instrument-and-hand-test-and-device')

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getTagListLabel(value) {
        const dictionary = {

          "1000002701": `好`,
          "1000002702": `良`,
          "1000002703": `差`,
          "1000002704": `非常差`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      downLoadImage(){
        if(this.imgUrl!==null) {
          for (let i = 0; i < this.imgUrl.length; i++) {
            downLoadImageFromUrl(this.imgUrl[i]);
          }
        }
        this.$refs['modal-image'].hide();
      },

      onExportButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let httpOption = this.$refs.taskVuetable.httpOptions;
        this.imgUrl = [];
        this.params = {
          'locale' : this.getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        this.link = `task/history-task/generate`;
        this.imgUrl = `task/history-task/generate/image`;
        this.name = this.$t('menu.history-task');
        this.isModalVisible = true;
      },
      onExport() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'locale' : this.getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `task/history-task/generate`;
        if (checkedIds.length > 0 && this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'History-Task', this.fileSelection);
          this.hideModal('model-export')
        }
      },

      onPrintButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let httpOption = this.$refs.taskVuetable.httpOptions;
        let params = {
          'locale' : this.getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };

        let link = `task/history-task/generate`;

          printFileFromServer(link, params);

      },

      getSiteOption() {
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        })
          .catch((error) => {
          });

      },

      getLocale() {

        return getLocale();
      },

      onRowClicked(taskNumber) {
        this.pageStatus = 'show';
        this.power = false;
        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.cntCartoon = 0;
        this.orderCartoon = 0;
        this.collectionLabel = [];

        let url1 = '';
        let url2 = '';
        let deviceImage, submitRects, cartoonRects;
        let colourInfo = null;
        let handGood = null, handAttached = null;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/task/history-task/get-one`, {
            'historyId': taskNumber,
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.showPage = response.data.data;
                this.apiBaseURL = apiBaseUrl;
                //if(this.showPage.workFlow.modeName
                let modeName;
                this.judgeStartTime = null;
                this.judgeDeviceName = null;
                this.judgeUserId = null;
                this.judgeUserName = null;
                this.handStartTime = null;
                this.handDeviceName = null;
                this.handUserName = null;

                this.thumbs = [];
                this.videos = [];
                this.images = [];
                this.imgRect = [];
                this.cartoonRect = [];
                this.rRects = [];
                this.imagesInfo = [];
                this.cartoonsInfo = [];
                this.imageRectR = [];
                this.imageRectL = [];
                this.cartoonRectR = [];
                this.cartoonRectL = [];
                this.collectionLabel = [];
                this.handGoodExpanded = [];
                this.handGoodDataCodeExpanded = [];

                // if(this.showPage.handAppraise === '1000001802'){
                //   if(this.showPage.handAppraiseSecond  !== '1000001801') {
                //     this.showPage.handAppraise = '1000001801';
                //   }
                //   else {
                //     this.showPage.handAppraise= '1000001803';
                //   }
                // }
                // else {
                //   if(this.showPage.handAppraiseSecond  === '1000001801') {
                //     this.showPage.handAppraise = '1000001802';
                //   } else {
                //     this.showPage.handAppraise = null;
                //   }
                // }

                // this.conclusionType = null;
                // if (this.showPage.serCheckResultList.length !== 0) {
                //   this.conclusionType = this.showPage.serCheckResultList[0].conclusionType;
                // }

                deviceImage = [];
                submitRects = [];
                colourInfo = this.showPage.platFormCheckParams;

                if (this.showPage.serScan !== undefined && this.showPage.serScan !== null) {
                  if (this.showPage.serScan.scanDeviceImages !== null) {
                    deviceImage = this.showPage.serScan.scanDeviceImages;
                    deviceImage = JSON.parse(deviceImage);
                  }
                }

                if (this.showPage.serJudgeGraph !== undefined && this.showPage.serJudgeGraph !== null) {

                  if(this.showPage.serJudgeGraph.judgeCartoonRects !== undefined && this.showPage.serJudgeGraph.judgeCartoonRects !== null) {
                    cartoonRects = this.showPage.serJudgeGraph.judgeCartoonRects;
                    cartoonRects = JSON.parse(cartoonRects);
                  }

                  if (this.showPage.serJudgeGraph.judgeSubmitrects !== undefined &&  this.showPage.serJudgeGraph.judgeSubmitrects !== null) {
                    submitRects = this.showPage.serJudgeGraph.judgeSubmitrects;
                    submitRects = JSON.parse(submitRects);
                  }
                }

                this.cntCartoon = Math.floor(deviceImage.length / 2);

                if (deviceImage !== null) {
                  for (let i = 0; i < deviceImage.length; i++) {
                    if (i < 2) {
                      this.imagesInfo.push({
                        rateWidth: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 1 / deviceImage[i].width :0,
                        rateHeight: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 1 / deviceImage[i].height :0,
                        imageUrl: deviceImage[i].cartoon,
                        imageRect: deviceImage[i].cartoonRects,
                        colorRect: colourInfo.scanRecogniseColour,
                        colorAdd: colourInfo.judgeRecogniseColour,
                        colorDel: colourInfo.displayDeleteSuspicionColour,
                        displayDel: colourInfo.displayDeleteSuspicion,
                        rectsAdd: cartoonRects !=null && cartoonRects[i] != undefined? cartoonRects[i].rectsAdded : null,
                        rectsDel: cartoonRects !=null && cartoonRects[i] != undefined?  cartoonRects[i].rectsDeleted : null
                      });
                    }


                      this.cartoonsInfo.push({
                        rateWidth: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 1 / deviceImage[i].width :0,
                        rateHeight: deviceImage[i].width != 0 && deviceImage[i].width !=null ?  1 / deviceImage[i].height :0,
                        imageUrl: deviceImage[i].image,
                        imageRect: deviceImage[i].imageRects,
                        colorRect: colourInfo.scanRecogniseColour,
                        colorAdd: colourInfo.judgeRecogniseColour,
                        colorDel: colourInfo.displayDeleteSuspicionColour,
                        displayDel: colourInfo.displayDeleteSuspicion,
                        rectsAdd: submitRects !=null && submitRects[i] != undefined? submitRects[i].rectsAdded : null,
                        rectsDel: submitRects !=null && submitRects[i] != undefined?  submitRects[i].rectsDeleted : null
                      });

                  }
                }

                if (this.imagesInfo[0] !== undefined) {
                  if (this.imagesInfo[0].imageRect != null) {
                    if(this.imagesInfo[0].rectsDel != null){
                      for (let i = 0; i < this.imagesInfo[0].imageRect.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[0].rectsDel.length; j++) {
                          if(this.imagesInfo[0].imageRect[i].x === this.imagesInfo[0].rectsDel[j].x && this.imagesInfo[0].imageRect[i].y === this.imagesInfo[0].rectsDel[j].y && this.imagesInfo[0].imageRect[i].width === this.imagesInfo[0].rectsDel[j].width && this.imagesInfo[0].imageRect[i].height === this.imagesInfo[0].rectsDel[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imageRectL.push({
                            x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].x,
                            y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].y,
                            width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].width,
                            height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].height,
                            colour: this.imagesInfo[0].colorRect,
                          });
                        }
                      }
                      for (let i = 0; i < this.imagesInfo[0].rectsDel.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[0].imageRect.length; j++) {
                          if(this.imagesInfo[0].rectsDel[i].x === this.imagesInfo[0].imageRect[j].x && this.imagesInfo[0].rectsDel[i].y === this.imagesInfo[0].imageRect[j].y && this.imagesInfo[0].rectsDel[i].width === this.imagesInfo[0].imageRect[j].width && this.imagesInfo[0].rectsDel[i].height === this.imagesInfo[0].imageRect[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imagesInfo[0].rectsDel[i].x = 0;
                          this.imagesInfo[0].rectsDel[i].y = 0;
                          this.imagesInfo[0].rectsDel[i].width = 0;
                          this.imagesInfo[0].rectsDel[i].height = 0;
                        }
                      }
                    }
                    else {
                      for (let i = 0; i < this.imagesInfo[0].imageRect.length; i++) {
                        this.imageRectL.push({
                          x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].x,
                          y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].y,
                          width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].width,
                          height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].height,
                          colour: this.imagesInfo[0].colorRect,
                        });
                      }
                    }
                  }
                  url1 = this.imagesInfo[0].imageUrl;
                  if (this.imagesInfo[0].rectsAdd != null) {
                    for (let i = 0; i < this.imagesInfo[0].rectsAdd.length; i++) {

                      this.imageRectL.push({
                        x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsAdd[i].x,
                        y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsAdd[i].y,
                        width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsAdd[i].width,
                        height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsAdd[i].height,
                        colour: this.imagesInfo[0].colorAdd,
                      });
                    }
                  }

                  if (this.imagesInfo[0].displayDel === '1000000601') {
                    if (this.imagesInfo[0].rectsDel != null) {
                      for (let i = 0; i < this.imagesInfo[0].rectsDel.length; i++) {

                        this.imageRectL.push({
                          x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsDel[i].x,
                          y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsDel[i].y,
                          width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsDel[i].width,
                          height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsDel[i].height,
                          colour: this.imagesInfo[0].colorDel,
                        });
                      }
                    }
                  }
                }

                if (this.imagesInfo[1] !== undefined) {
                  if (this.imagesInfo[1].imageRect != null) {
                    if(this.imagesInfo[1].rectsDel != null){
                      for (let i = 0; i < this.imagesInfo[1].imageRect.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[1].rectsDel.length; j++) {
                          if(this.imagesInfo[1].imageRect[i].x === this.imagesInfo[1].rectsDel[j].x && this.imagesInfo[1].imageRect[i].y === this.imagesInfo[1].rectsDel[j].y && this.imagesInfo[1].imageRect[i].width === this.imagesInfo[1].rectsDel[j].width && this.imagesInfo[1].imageRect[i].height === this.imagesInfo[1].rectsDel[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imageRectR.push({
                            x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].x,
                            y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].y,
                            width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].width,
                            height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].height,
                            colour: this.imagesInfo[1].colorRect,
                          });
                        }
                      }
                      for (let i = 0; i < this.imagesInfo[1].rectsDel.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[1].imageRect.length; j++) {
                          if(this.imagesInfo[1].rectsDel[i].x === this.imagesInfo[1].imageRect[j].x && this.imagesInfo[1].rectsDel[i].y === this.imagesInfo[1].imageRect[j].y && this.imagesInfo[1].rectsDel[i].width === this.imagesInfo[1].imageRect[j].width && this.imagesInfo[1].rectsDel[i].height === this.imagesInfo[1].imageRect[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imagesInfo[1].rectsDel[i].x = 0;
                          this.imagesInfo[1].rectsDel[i].y = 0;
                          this.imagesInfo[1].rectsDel[i].width = 0;
                          this.imagesInfo[1].rectsDel[i].height = 0;
                        }
                      }
                    }
                    else {
                      for (let i = 0; i < this.imagesInfo[1].imageRect.length; i++) {
                        this.imageRectR.push({
                          x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].x,
                          y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].y,
                          width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].width,
                          height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].height,
                          colour: this.imagesInfo[1].colorRect,
                        });
                      }
                    }
                  }
                  url2 = this.imagesInfo[1].imageUrl;
                  if (this.imagesInfo[1].rectsAdd != null) {
                    for (let i = 0; i < this.imagesInfo[1].rectsAdd.length; i++) {
                      this.imageRectR.push({
                        x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsAdd[i].x,
                        y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsAdd[i].y,
                        width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsAdd[i].width,
                        height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsAdd[i].height,
                        colour: this.imagesInfo[1].colorAdd,
                      });
                    }
                  }

                  if (this.imagesInfo[1].displayDel === '1000000601' && this.imagesInfo[1].rectsDel != null) {
                    for (let i = 0; i < this.imagesInfo[1].rectsDel.length; i++) {
                      this.imageRectR.push({
                        x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsDel[i].x,
                        y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsDel[i].y,
                        width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsDel[i].width,
                        height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsDel[i].height,
                        colour: this.imagesInfo[1].colorDel,
                      });
                    }
                  }
                }

                loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

                let handGoodsStr = this.showPage.handGoods;
                let handAttactedStr = this.showPage.handAttached;

                if (handGoodsStr !== null) {
                  handGood = handGoodsStr.split(",");
                }

                if (handAttactedStr !== null) {
                  handAttached = handAttactedStr.split(",");
                }

                let k = 0;
                if (handGood !== null) {
                  for (let i = 0; i < handGood.length; i++) {
                    for (let j = 0; j < 5; j++) {
                      if (handGood[i] === this.handGoodDataCode[j]) {
                        this.handGoodExpanded[k] = true;
                        this.handGoodDataCodeExpanded[k] = this.handGoodDataCode[j];
                        k++;
                      }
                    }
                  }
                }


                //getting media data from server.
                if (handAttached !== null) {
                  for (let i = 0; i < handAttached.length; i++) {
                    let iHandAttached = handAttached[i].split(".");
                    if (iHandAttached[1] === "png" || iHandAttached[1] === "jpg") {
                      this.thumbs.push({
                        name: iHandAttached[0],
                        src: handAttached[i]
                      });
                      this.images.push(handAttached[i]);
                      /* this.thumbs[k].name = iHandAttached[0];
                    this.thumbs[k].src = handAttached[i];
                    this.images[k] = handAttached[i];*/

                    } else {
                      this.videos.push({
                        name: iHandAttached[0],
                        src: handAttached[i],
                        poster: '',//todo if client need to show different poster for each videos, should get its poster image from server.
                      });
                    }
                  }
                }

                break;// okay
            }
          })
          .catch((error) => {
          });

        this.history_id = taskNumber;

      },

      showCollectionView() {
        this.collectionLabel = [];
        this.$refs['model-collection'].show();
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onCollectionClicked(history_id) {

        let tagList = [];

        if(this.collectionLabel.length === 0){
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`personal-inspection.select-tag`), {
            duration: 3000,
            permanent: false
          });
          return;
        }
        for (let i = 0; i < this.collectionLabel.length; i++) {
          tagList[i] = this.collectionLabel[i].value;
        }

        let loginfo = getLoginInfo();
        let userId = loginfo.user.id;
        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/create`, {
            'historyId': history_id,
            'userId': userId,
            'tagList': tagList
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.$notify('success', this.$t('permission-management.success'), this.$t(`personal-inspection.activate`), {
                  duration: 3000,
                  permanent: false
                });// okay
                this.hideModal('model-collection');
                this.onRowClicked(history_id);
                break;
              case responseMessages['exist-knowledge']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`personal-inspection.exist-knowledge`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          });
      },

      getDateTimeFormat2(dataTime) {
        if (dataTime == null) {
          return '';
        }
        return getDateTimeWithFormat(dataTime);
      },

      getDateTimeFormat(dataTime) {
        if (dataTime == null) {
          return '';
        }
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onSearchButton() {
        if(this.filter.startTime !== null && this.filter.endTime !== null) {

          if (this.filter.startTime >= this.filter.endTime) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`maintenance-management.process-task.time-select`), {
              duration: 3000,
              permanent: false
            });
            return;
          }

        }
        this.$refs.taskVuetable.refresh();
      },
      autoUpdate() {
        if(this.filter.startTime !== null && this.filter.endTime !== null) {

          if (this.filter.startTime >= this.filter.endTime) {
            return;
          }

        }
        this.$refs.taskVuetable.reload();
      },

      onResetButton() {
        this.filter = {
          taskNumber: null,
          mode: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
        };
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
          this.renderedCheckList.push(data.data[i].historyId);
          transformed.data.push(temp);
        }

        return transformed

      },

      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        this.apiUrl = apiUrl;
        this.httpOption = httpOptions;
        this.renderedCheckList = [];
        this.changeCheckAllStatus();
        this.changeCheckAllStatus();

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          filter: this.filter,
          sort: httpOptions.params.sort,
          perPage: this.taskVuetableItems.perPage,
        });
      },

      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
        this.changeCheckAllStatus();
      },

      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page);
        this.changeCheckAllStatus();
      }
    }
  }
</script>
