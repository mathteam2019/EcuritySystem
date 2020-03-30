<script>
  import vSelect from 'vue-select';
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../utils";
  import {validationMixin} from "vuelidate";
  import {downLoadFileFromServer, downLoadImageFromUrl, getApiManagerError} from "../../api";
  import {apiBaseUrl} from "../../constants/config";
  import {responseMessages} from "../../constants/response-messages";

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');
  //const fs = require('fs-extra');

  export default {
    name: 'modal',
    props: {
      link: {
        type: String
      },
      params: {
        type: Object
      },
      name: {
        type: String
      },
      url: {
        type: Array
      },
      imgLink: {
        type: String
      },
    },
    components: {
      'v-select': vSelect,
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },

    data() {
      return {
        fileSelection: [],
        imgUrl :[],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
      }
    },

    methods: {
      downLoadImage(){
        if(this.imgUrl.length!==0) {
          for (let i = 0; i < this.imgUrl.length; i++) {
            downLoadImageFromUrl(this.imgUrl[i]);
          }
        }
      },
      exportFile() {
        //this.exported = true;
        let url="/assets/img/profile.png";
        // console.log(this.url);
        // if(this.url!==undefined){
        //   if(this.url!==null) {
        //     for (let i = 0; i < this.url.length; i++) {
        //       downLoadImageFromUrl(this.url[i]);
        //     }
        //   }
        // }
        //downLoadImageFromUrl(url);
        if(this.params.isAll===true&&this.params.idList===""){
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-messages.select-data`), {
            duration: 3000,
            permanent: false
          });
        }
        if(this.fileSelection.length === 0){
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-messages.select-file-type`), {
            duration: 3000,
            permanent: false
          });
        }
        if (this.fileSelection.length !== 0 && !(this.params.isAll===true&&this.params.idList==="")) {
          this.imgUrl = [];
          if(this.imgLink!==undefined) {
            getApiManagerError()
              .post(`${apiBaseUrl}/` + this.imgLink, this.params).then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  if (data.enabledCartoon) {
                    data.cartoonImageList.forEach(item => {
                      this.imgUrl.push(item);
                    });
                  }
                  if (data.enabledOriginal) {
                    //this.imgUrl = data.originalImageList;
                    data.originalImageList.forEach(item => {
                      this.imgUrl.push(item);
                    });
                  }
                  this.downLoadImage();
                  break;
              }
              })
              .catch((error) => {
              });
          }
          downLoadFileFromServer(this.link, this.params, this.name, this.fileSelection);
          this.close();
        }
      },

      // readDirectory() {
      //   fs.readdir(this.dir, (err, dir) => {
      //     console.log(dir);
      //     for(let filePath of dir)
      //       console.log(filePath)
      //     this.files = dir
      //   })
      // },
      // selectFolder(e) {
      //   // var theFiles = e.target.files;
      //   // var relativePath = theFiles[0].webkitRelativePath;
      //   // var folder = relativePath.split("/");
      //   // alert(folder[0]);
      //   // const ipc = require('electron').ipcMain
      //   // const dialog = require('electron').dialog
      //   // ipc.on('open-file-dialog', function (event) {
      //   //   dialog.showOpenDialog({
      //   //     properties: ['openFile']
      //   //   }, function (files) {
      //   //     if (files) event.sender.send('selected-file', files)
      //   //   })
      //   // })
      // },

      close() {
        this.$emit('close');
      },

    },
  };
</script>

<template>

<!--  <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{-->
<!--    $t('permission-management.upload-image')}}-->
<!--  </b-button>-->
  <div class="modal-backdrop" style="display:block;">
    <div class="modal modal-select">
      <header class="modal-header header-font">
        <slot name="header">
          <label class="text-center text-large">
            {{ $t('permission-management.export') }}
          </label>
          <button
            type="button"
            class="btn-close"
            @click="close"
          >
            x
          </button>
        </slot>
      </header>
      <section class="modal-body">
        <slot name="body">
          <b-form-group class="mw-100 w-100 body-padding" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
<!--          <input type="file" ref="imgFile" webkitdirectory directory style="display: none"/>-->
        </slot>
      </section>
      <footer class="modal-footer">
        <slot name="footer">
          <b-button size="sm" variant="info default" @click="exportFile">
            <i class="icofont-gift"/>
            {{ $t('permission-management.export') }}
          </b-button>
          <b-button size="sm" variant="light default" @click="close">
            <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
          </b-button>
        </slot>
      </footer>
    </div>
  </div>
</template>

<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal-backdrop {
    opacity: 1;
  }

  .modal-select {
    top: calc((100% - 350px)/2) !important;
    left: calc((100% - 550px)/2) !important;
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: auto;
    display: flex;
    flex-direction: column;
    width: 550px;
    height: 350px;
  }

  .modal-header,
  .modal-footer {
    padding: 15px;
    display: flex;
  }

  .modal-header {
    border-bottom: 1px solid #eeeeee;
    justify-content: space-between;
  }

  .header-font{
    font-weight: bold;
    font-size: large;
  }

  .modal-footer {
    border-top: 1px solid #eeeeee;
    justify-content: flex-end;
  }

  .modal-body {
    position: relative;
    padding: 20px 10px;
  }

  .body-padding {
    padding-right: 2rem;
    padding-left: 2rem;
  }

  .btn-close {
    border: none;
    font-size: 18px;
    cursor: pointer;
    font-weight: bold;
    color: #000000;
    background: transparent;
  }

</style>
