<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>

    <b-card class="main-without-tab" v-if="pageStatus === 'table'">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.operation-mode')">
                  <b-form-select v-model="filter.operationMode" :options="operationModeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.status')">
                  <b-form-select v-model="filter.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.on-site')">
                  <b-form-select v-model="filter.onSite" :options="onSiteOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded" >
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="8" v-if="isExpanded">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.time')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>

              <b-col></b-col>
              <b-col></b-col>
              <b-col></b-col>


            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="taskVuetable"
                :api-mode="false"
                :data="tempData"
                data-path="data"
                pagination-path="pagination"
                :data-total="5"
                :per-page="5"
                :fields="taskVuetableItems.fields"
                class="table-hover"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="taskNumber" slot-scope="props">
                    <span class="cursor-p text-primary" @click="onRowClicked(props.rowData)">
                      {{props.rowData.taskNumber}}
                    </span>
                </template>
                <template slot="operationMode" slot-scope="props">
                  <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon" />
                  <b-img src="/assets/img/monitors_icon.svg" class="operation-icon" />
                  <b-img src="/assets/img/mobile_icon.svg" class="operation-icon" />
                </template>
                <template slot="taskResult" slot-scope="props">
                  <div v-if="props.rowData.taskResult === 'no-suspect'" style="color: green;">
                    {{$t('knowledge-base.no-suspect')}}
                  </div>
                  <div v-if="props.rowData.taskResult === 'seized'" style="color: red;">
                    {{$t('knowledge-base.seized')}}
                  </div>
                </template>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="taskVuetablePagination"
                @vuetable-pagination:change-page="onTaskVuetableChangePage"
                :initial-per-page="taskVuetableItems.perPage"
                @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>

      </div>
    </b-card>

    <div v-if="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col cols="3">
          <b-card class="pt-4 h-100">
            <b-row class="mb-1">
              <b-col>
                <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                <b-img src="/assets/img/monitors_icon.svg" class="operation-icon ml-2"/>
                <b-img src="/assets/img/mobile_icon.svg" class="operation-icon ml-2"/>
              </b-col>
              <b-col class="text-right icon-container">
                <span><i class="icofont-star"></i></span>
                <span><i class="icofont-search-user"></i></span>
                <span><i class="icofont-female"></i></span>
              </b-col>
            </b-row>

            <b-row class="mb-4">
              <b-col>
                <b-img src="/assets/img/scan-rl.gif" fluid-grow></b-img>
              </b-col>
              <b-col>
                <b-img src="/assets/img/scan-lr.gif" fluid-grow></b-img>
              </b-col>
            </b-row>

            <b-row class="mb-2">
              <b-col class="control-group">
                <div class="control-btn-wrapper">

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
                  <div class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" theme="custom" color="info"></switches>
                  </div>
                </div>
              </b-col>
            </b-row>


          </b-card>
        </b-col>
        <b-col cols="9">
          <b-card class="h-100 d-flex flex-column right-card">
            <div class="history-chart">
              <div>

                <div class="part">
                  <div class="left">
                    <div>开始</div>
                  </div>
                  <div class="right">
                    <div>Start</div>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>扫描</div>
                    <div>张三</div>
                  </div>
                  <div class="right">
                    <div>Scanning</div>
                    <div>zhang san</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>判图</div>
                    <div>李四</div>
                  </div>
                  <div class="right">
                    <div>Decision diagram</div>
                    <div>Li si</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>查验</div>
                    <div>王五</div>
                  </div>
                  <div class="right">
                    <div>Inspection</div>
                    <div>Wang wu</div>
                  </div>
                  <div class="top-date">2019-09-21 11:43:55</div>
                  <div class="bottom-date">2019-09-21 11:43:55</div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>结束</div>
                  </div>
                  <div class="right">
                    <div>End</div>
                  </div>
                </div>

              </div>

            </div>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>HR201909010001</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>北京首都机场</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>安检仪001</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>男</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scanned-image')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>ATR</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <div>
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon" />
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon ml-2" />
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon ml-2" />
                  </div>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.status')}}
                  </template>
                  <label>全部</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.guide')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>张三</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.atr-conclusion')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>无嫌疑</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.foot-alarm')}}
                  </template>
                  <label>无</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>20190921 10:40:05</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-end-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>20190921 10:40:05</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.dispatch-timeout')}}
                  </template>
                  <label>无</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>TC0001</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>ATR</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>无嫌疑</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-timeout')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>无</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>20190921 10:41:05</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station-identification')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>全部</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-end-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>20190921 10:41:05</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judge')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>李四</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>张三</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>20190921 10:42:05</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-checker')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>王五</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-result')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>无嫌疑</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.item-level')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>一级</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>误报</label>
                </b-form-group>
              </b-col>
              <b-col></b-col>
              <b-col></b-col>
              <b-col></b-col>
            </b-row>

            <b-row class="flex-grow-1 d-flex align-items-end">
              <b-col>
                <label class="font-weight-bold">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`" @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 75px; height: 60px;"  :alt="thumb.name"/>
                    <label class="d-block text-center mt-2">{{thumb.name}}</label>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()" />
                </b-row>
              </b-col>

              <b-col class="seized-list">
                <label class="font-weight-bold">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="d-flex justify-content-end">
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff0000">
                      <b-img src="/assets/img/pistol_icon.svg" class="contraband-icon" />
                      <span class="contraband-count">2</span>
                      <span class="contraband-category">{{$t('personal-inspection.firearms')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff4e00">
                      <b-img src="/assets/img/drug_icon.svg" class="contraband-icon" />
                      <span class="contraband-count">1</span>
                      <span class="contraband-category">{{$t('personal-inspection.drug')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff7e00">
                      <b-img src="/assets/img/knife_icon.svg" class="contraband-icon" />
                      <span class="contraband-count">0</span>
                      <span class="contraband-category">{{$t('personal-inspection.dagger')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ffae00">
                      <b-img src="/assets/img/cigarette_icon.svg" class="contraband-icon" />
                      <span class="contraband-count">9</span>
                      <span class="contraband-category">{{$t('personal-inspection.firearms')}}</span>
                    </div>
                  </b-col>
                </b-row>
              </b-col>
            </b-row>

            <b-row>
              <b-col cols="12" class="align-self-end text-right mt-3">
                <b-button size="sm" variant="orange default" @click="pageStatus='table'">
                  <i class="icofont-gift"></i>
                  {{ $t('personal-inspection.collection') }}
                </b-button>
                <b-button size="sm" variant="info default" @click="pageStatus='table'">
                  <i class="icofont-long-arrow-left"></i>
                  {{ $t('personal-inspection.return') }}
                </b-button>
              </b-col>
            </b-row>

          </b-card>
        </b-col>
      </b-row>
    </div>



  </div>
</template>

<style lang="scss">
  span.cursor-p {
    cursor: pointer !important;
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
        margin-bottom: 24px;

        img {
          $size: 40px;
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
      .switch-wrapper{
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
        width: 80px;
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
          position: absolute;
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
    import {getDirection} from "../../../utils";
    import _ from "lodash";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';
    import {validationMixin} from 'vuelidate';
    import VTree from 'vue-tree-halower';
    import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
    import Switches from 'vue-switches';
    import {LightGallery} from 'vue-light-gallery';

    const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
            'switches': Switches,
            'light-gallery': LightGallery
        },
        mounted() {

        },
        data() {
            return {
                isExpanded:false,
                pageStatus: 'table',
                filter: {
                    operationMode: null,
                    status: null,
                    onSite: null
                    // TODO: search filter
                },
                // TODO: select options
                operationModeOptions: [
                    {value: null, text: this.$t('personal-inspection.all')},
                    {value: 'security', text: this.$t('personal-inspection.security-instrument')},
                    {value: 'security+hand', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
                    {value: 'security+hand+device', text: this.$t('personal-inspection.security-instrument-and-hand-test-and-device')},
                ],
                statusOptions: [
                    {value: null, text: this.$t('personal-inspection.all')},
                    {value: 'pending-dispatch', text: this.$t('personal-inspection.pending-dispatch')},
                    {value: 'pending-review', text: this.$t('personal-inspection.pending-review')},
                    {value: 'while-review', text: this.$t('personal-inspection.while-review')},
                    {value: 'pending-inspection', text: this.$t('personal-inspection.pending-inspection')},
                    {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')}
                ],
                onSiteOptions: [
                    {value: null, text: this.$t('personal-inspection.all')},
                    {value: 'pending-dispatch', text: this.$t('personal-inspection.task-pending-dispatch')},
                    {value: 'dispatch', text: this.$t('personal-inspection.task-dispatched')},
                    {value: 'while-review', text: this.$t('personal-inspection.while-review')},
                    {value: 'reviewed', text: this.$t('personal-inspection.reviewed')},
                    {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')},
                ],
                // TODO: refactor temp table data to api mode
                tempData: {
                    pagination: {
                        total: 5,
                        per_page: 5,
                        current_page: 1,
                        last_page: 1,
                        from: 1,
                        to: 5
                    },
                    data: [
                        {
                            id: 1,
                            taskNumber: 'HR201909210001',
                            image: '',
                            taskResult: 'no-suspect', // or 'seized'
                            onSize: '',
                            securityInstrument: '',
                            guide: '张三',
                            scanStartTime: '2019-10-23. 10:30',
                            scanEndTime: '2019-10-23. 10:30',
                            judgementStation: '丹东站',
                            judge: '张三',
                            judgementStartTime: '2019-10-23. 10:30',
                            judgementEndTime: '2019-10-23. 10:30',
                            handCheckStation: '丹东站',
                            handChecker: '张三',
                            handCheckStartTime: '2019-10-23. 10:30'
                        },
                        {
                            id: 2,
                            taskNumber: 'HR201909210001',
                            image: '',
                            taskResult: 'no-suspect', // or 'seized'
                            onSize: '',
                            securityInstrument: '',
                            guide: '张三',
                            scanStartTime: '2019-10-23. 10:30',
                            scanEndTime: '2019-10-23. 10:30',
                            judgementStation: '丹东站',
                            judge: '张三',
                            judgementStartTime: '2019-10-23. 10:30',
                            judgementEndTime: '2019-10-23. 10:30',
                            handCheckStation: '丹东站',
                            handChecker: '张三',
                            handCheckStartTime: '2019-10-23. 10:30'
                        },
                        {
                            id: 3,
                            taskNumber: 'HR201909210001',
                            image: '',
                            taskResult: 'no-suspect', // or 'seized'
                            onSize: '',
                            securityInstrument: '',
                            guide: '张三',
                            scanStartTime: '2019-10-23. 10:30',
                            scanEndTime: '2019-10-23. 10:30',
                            judgementStation: '丹东站',
                            judge: '张三',
                            judgementStartTime: '2019-10-23. 10:30',
                            judgementEndTime: '2019-10-23. 10:30',
                            handCheckStation: '丹东站',
                            handChecker: '张三',
                            handCheckStartTime: '2019-10-23. 10:30'
                        },
                        {
                            id: 4,
                            taskNumber: 'HR201909210001',
                            image: '',
                            taskResult: 'seized',
                            onSize: '',
                            securityInstrument: '',
                            guide: '张三',
                            scanStartTime: '2019-10-23. 10:30',
                            scanEndTime: '2019-10-23. 10:30',
                            judgementStation: '丹东站',
                            judge: '张三',
                            judgementStartTime: '2019-10-23. 10:30',
                            judgementEndTime: '2019-10-23. 10:30',
                            handCheckStation: '丹东站',
                            handChecker: '张三',
                            handCheckStartTime: '2019-10-23. 10:30'
                        },
                        {
                            id: 5,
                            taskNumber: 'HR201909210001',
                            image: '',
                            taskResult: 'seized',
                            onSize: '',
                            securityInstrument: '',
                            guide: '张三',
                            scanStartTime: '2019-10-23. 10:30',
                            scanEndTime: '2019-10-23. 10:30',
                            judgementStation: '丹东站',
                            judge: '张三',
                            judgementStartTime: '2019-10-23. 10:30',
                            judgementEndTime: '2019-10-23. 10:30',
                            handCheckStation: '丹东站',
                            handChecker: '张三',
                            handCheckStartTime: '2019-10-23. 10:30'
                        },
                    ]
                },
                taskVuetableItems: {
                    apiUrl: `${apiBaseUrl}/...`,
                    fields: [
                        {
                            name: '__checkbox',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'id',
                            title: this.$t('personal-inspection.serial-number'),
                            sortField: 'id',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:taskNumber',
                            title: this.$t('personal-inspection.task-number'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'image',
                            title: this.$t('personal-inspection.image'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:operationMode',
                            title: this.$t('personal-inspection.operation-mode'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:taskResult',
                            title: this.$t('personal-inspection.task-result'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'onSite',
                            title: this.$t('personal-inspection.on-site'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'securityInstrument',
                            title: this.$t('personal-inspection.security-instrument'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'guide',
                            title: this.$t('personal-inspection.guide'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scanStartTime',
                            title: this.$t('personal-inspection.scan-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scanEndTime',
                            title: this.$t('personal-inspection.scan-end-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementStation',
                            title: this.$t('personal-inspection.judgement-station'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judge',
                            title: this.$t('personal-inspection.judge'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementStartTime',
                            title: this.$t('personal-inspection.judgement-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementEndTime',
                            title: this.$t('personal-inspection.judgement-end-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'handCheckStation',
                            title: this.$t('personal-inspection.hand-check-station'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'handChecker',
                            title: this.$t('personal-inspection.hand-checker'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'handCheckStartTime',
                            title: this.$t('personal-inspection.hand-check-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                    ],
                    perPage: 5,
                },
                power: true,

                thumbs: [
                    {name: '001.jpg', src: '/assets/img/drug-thumb.jpg'},
                    {name: '001.jpg', src: '/assets/img/drug-thumb.jpg'},
                    {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'},
                    {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'},
                    {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'}
                ],
                images: [
                    '/assets/img/drug.jpg',
                    '/assets/img/drug.jpg',
                    '/assets/img/glock.jpg',
                    '/assets/img/glock.jpg',
                    '/assets/img/glock.jpg',
                ],
                photoIndex: null


            }
        },
        watch: {
            'vuetableItems.perPage': function (newVal) {
                this.$refs.vuetable.refresh();
            },
            'operatingLogTableItems.perPage': function (newVal) {
                this.$refs.operatingLogTable.refresh();
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
            onTableListPage() {
                this.pageStatus = 'table';
            },
            onRowClicked() {
                this.pageStatus = 'show';
            },
            onSaveUserPage() {
                this.submitted = true;
                this.$v.$touch();
                if (this.$v.$invalid) {
                    return;
                }

                const formData = new FormData();
                for (let key in this.profileForm) {
                    if (key !== 'portrait')
                        formData.append(key, this.profileForm[key]);
                    else if (this.profileForm['portrait'] !== null)
                        formData.append(key, this.profileForm[key], this.profileForm[key].name);
                }
                // call api
                let finalLink = this.profileForm.userId > 0 ? 'modify' : 'create';
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/user-management/user/` + finalLink, formData)
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.profileForm.userId > 0 ? this.$t(`permission-management.user-created-successfully`) : this.$t(`permission-management.user-modify-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                this.onInitialUserData();
                                // back to table
                                this.pageStatus = 'table';
                                break;
                            case responseMessages['used-user-account']://duplicated user account
                                this.$notify('success', this.$t('permission-management.failed'), this.$t(`permission-management.user-account-already-used`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                break;
                        }
                    })
                    .catch((error) => {
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
                    case 'active':
                    case 'unblock':
                        this.fnChangeItemStatus(userId, action);
                        break;
                    case 'inactive':
                    case 'blocked':
                        this.fnShowConfDiaglog(userId, action);
                        break;
                    case 'group-remove':
                        this.fnShowUserGroupConfDiaglog(data);
                        break;
                }
            },
            onThumbClick(index) {
                this.photoIndex = index;
            },
            handleHide() {
                this.photoIndex = null;
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
                if (status === 'unblock' || status === 'reset-password')
                    status = 'inactive';
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

                                this.$refs.vuetable.refresh();

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

            },
            onResetButton() {

            },
            onInitialUserData() {
                this.profileForm = {
                    status: 'inactive',
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
                    category: '',
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
                    temp.orgName = fnGetOrgFullName(temp.org);
                    transformed.data.push(temp)
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
                                case responseMessages['has-children']: // okay
                                    this.$notify('success', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
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

                transformed.operatingLogPagination = {
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
                    filter: {
                        userName: this.filter.userName,
                        status: this.filter.status,
                        orgId: this.filter.orgId,
                        category: this.filter.category,
                    }
                });
            },
            onUserTablePaginationData(paginationData) {
                this.$refs.pagination.setPaginationData(paginationData)
            },
            onUserTableChangePage(page) {
                this.$refs.vuetable.changePage(page)
            },
            onGroupFormSubmit() {
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, this.groupForm)
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.group-created-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });

                                this.$refs.userGroupTable.refresh();

                                break;

                        }
                    })
                    .catch((error) => {
                    })
                    .finally(() => {
                        //
                        this.groupForm = {
                            groupName: null,
                            groupNumber: null,
                            status:'create'
                        };
                    });
            },
            userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

                return getApiManager().post(apiUrl, {
                    currentPage: httpOptions.params.page,
                    perPage: this.operatingLogTableItems.perPage,
                    filter: {
                        groupName: this.groupFilter.name,
                    }
                });
            },
            onTaskVuetablePaginationData(paginationData) {
                this.$refs.taskVuetablePagination.setPaginationData(paginationData)
            },
            onTaskVuetableChangePage(page) {
                this.$refs.taskVuetable.changePage(page)
            },
            onUserGroupTableRowClick(dataItems) {
                this.selectedUserGroupItem = dataItems;
                this.groupForm.status = 'modify';
                console.log(this.selectedUserGroupItem);
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
                    name:null
                };
                this.$refs.userGroupTable.refresh();
            },
            onUserGroupCreateButton(){
                this.selectedUserGroupItem = {
                    users:[]
                };
                this.groupForm = {
                    groupNumber:null,
                    groupName:null,
                    status:'create'
                }
            },
            onClickDeleteUserGroup(){
                this.fnShowUserGroupConfDiaglog(this.selectedUserGroupItem);
            },
            onClickCreateUserGroup() {
                if(this.selectedUserGroupItem) {
                    let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
                    let userGroupUserIds = [];
                    checkedNodes.forEach((node) => {
                        if(node.isUser)userGroupUserIds.push(node.userId);
                    });
                    if(userGroupUserIds.length==0){
                        return ;
                    }
                    getApiManager()
                        .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, {
                            'groupName':this.groupForm.groupName,
                            'groupNumber':this.groupForm.groupNumber,
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
                                    break;
                                default:

                            }
                        })
                        .catch((error) => {

                        }).finally(() => {
                        //
                        this.groupForm = {
                            groupName: null,
                            groupNumber: null,
                            status:'create'
                        };
                    });
                }
            },
            onClickModifyUserGroup() {
                if(this.selectedUserGroupItem) {
                    let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
                    let userGroupUserIds = [];
                    checkedNodes.forEach((node) => {
                        if(node.isUser)userGroupUserIds.push(node.userId);
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
                                    this.$refs.userGroupTable.refresh();
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
