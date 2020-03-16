<template>
  <div class="dashboard-container d-flex flex-column">
    <b-row>
      <!--            <b-col cols="12">-->
      <!--              <b-img src="/assets/img/admin_home_main_banner.png" fluid-grow class="shadow-sm"/>-->
      <!--            </b-col>-->

      <b-col cols="12" style="">
        <div class="bg-dashboard" style="padding-top: 5rem;">
          <h3 style="color: #1a3035" class="font-weight-bold my-4">
            <label style="margin-left: 8rem; font-size: 4rem;">{{$t('dashboard.title')}}</label>
          </h3>
          <h5 style="color: rgb(131, 146, 165);" class="font-weight-bold my-4 mb-5 pb-4">
            <label style="margin-left: 10rem; font-size: 1rem; float:left;">{{$t('dashboard.title-label')}}</label>
          </h5>
        </div>
      </b-col>

    </b-row>
    <b-row class="d-flex flex-grow-1 align-items-center kcb-mt-20">
      <b-col cols="5">
        <div class="bg-image" style="padding-top: 2rem;">
          <div style="color: rgb(0, 124, 212); margin-top: 1rem !important;">
            <label style="margin-left: 36%; font-size: 2rem;">{{$t('dashboard.image-title')}}</label>
          </div>
          <div style="color: rgb(88, 93, 99);">
            <label style="margin-top: 0.5rem; margin-left: 25%; font-size:0.9rem; float:left; padding-left: 6rem;width: 22rem">{{$t('dashboard.image-label-1')}}</label>
          </div>
          <div style="color: rgb(88, 93, 99);">
            <label
              style="margin-left: 21rem; font-size: 0.9rem; float:left; padding-left: 6rem;width: 22rem; position: absolute; right: 2rem; bottom: 2rem">{{$t('dashboard.image-label-2')}}</label>
          </div>
        </div>
      </b-col>
      <!--      <b-col cols="5">-->
      <!--        <b-img src="/assets/img/home_bottom_banner.png" fluid-grow class="shadow-sm"/>-->
      <!--      </b-col>-->

      <b-col cols="7" class="pl-5">
        <b-row>
          <b-col cols="6" class="article d-flex">
            <div class="article-icon-container">
              <b-img src="/assets/img/rocket_icon.svg"/>
            </div>
            <div class="ml-5 mt-2 mr-3">
              <h5>{{$t('dashboard.article-title-1')}}</h5>
              <h6 v-html="$t('dashboard.article-extract-1')"></h6>
            </div>
          </b-col>
          <b-col cols="6" class="article d-flex">
            <div class="article-icon-container">
              <b-img src="/assets/img/rocket_icon.svg"/>
            </div>
            <div class="ml-5 mt-2 mr-3">
              <h5>{{$t('dashboard.article-title-2')}}</h5>
              <h6 v-html="$t('dashboard.article-extract-2')"></h6>
            </div>
          </b-col>
        </b-row>
        <b-row class="mt-5">
          <b-col cols="6" class="article d-flex">
            <div class="article-icon-container">
              <b-img src="/assets/img/rocket_icon.svg"/>
            </div>
            <div class="ml-5 mt-2 mr-3">
              <h5>{{$t('dashboard.article-title-3')}}</h5>
              <h6 v-html="$t('dashboard.article-extract-3')"></h6>
            </div>
          </b-col>
          <b-col cols="6" class="article d-flex">
            <div class="article-icon-container">
              <b-img src="/assets/img/rocket_icon.svg"/>
            </div>
            <div class="ml-5 mt-2 mr-3">
              <h5>{{$t('dashboard.article-title-4')}}</h5>
              <h6 v-html="$t('dashboard.article-extract-4')"></h6>
            </div>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
  </div>
</template>
<script>
  // import {getLocale} from '../../../utils';
</script>
<style>
  .bg-dashboard {
    height: fit-content;
    height: 400px;
    box-shadow: 0 .125rem .25rem rgba(0, 0, 0, .075) !important;
    border-radius: 10px;
    background: url("../../../assets/img/admin_home_main_banner.png") no-repeat center;

    & > div {
      background: transparent !important;
    }

  }

  .bg-image {
    height: fit-content;
    height: 400px;
    box-shadow: 0 .125rem .25rem rgba(0, 0, 0, .075) !important;
    border-radius: 15px;
    background: url("../../../assets/img/home_bottom_banner.png") no-repeat center;

    & > div {
      background: transparent !important;
    }

  }

  .image-label-2 {
    margin-left: 21rem;
    margin-top: 8rem;
    font-size: 15px;
    float: left;
    padding-left: 6rem;
    width: 22rem
  }
  .image-label-1 {
    margin-left: 21rem;
    font-size: 15px;
    float: left;
    padding-left: 6rem;
    width: 22rem
  }
</style>

