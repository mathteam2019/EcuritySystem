<template>
  <b-input-group class="mb-3 colorpicker-chrome" ref="colorpicker">
    <b-form-input v-model="colorValue" @focus="showPicker()" @input="updateFromInput" :disabled="true" :style="disablePicker?'background-color: #e9ecef;':''"/>
    <b-input-group-text>
      <span class="current-color" :style="'background-color: ' + colorValue" @click="togglePicker()"></span>
      <chrome-picker :value="colors" @input="updateFromPicker" v-if="displayPicker" />
    </b-input-group-text>
  </b-input-group>
</template>

<style lang="scss">
  .colorpicker-chrome {
    .input-group-text {
      padding: .375rem .75rem;
    }
  }
</style>

<script>
    import Chrome from '../vue-color/src/components/Chrome';
    export default {
        components: {
            'chrome-picker': Chrome,
        },
        props: ['color', 'disablePicker'],
        data() {
            return {
                colors: {
                    hex: '#000000',
                },
                colorValue: '',
                displayPicker: false,
                disabled:false,

            }
        },
        mounted() {
            this.setColor(this.color || '#000000');
        },
        methods: {
            setColor(color) {
                this.updateColors(color);
                this.colorValue = color;
            },
            updateColors(color) {
                if(color.slice(0, 1) == '#') {
                    this.colors = {
                        hex: color
                    };
                }
                else if(color.slice(0, 4) == 'rgba') {
                    var rgba = color.replace(/^rgba?\(|\s+|\)$/g,'').split(','),
                        hex = '#' + ((1 << 24) + (parseInt(rgba[0]) << 16) + (parseInt(rgba[1]) << 8) + parseInt(rgba[2])).toString(16).slice(1);
                    this.colors = {
                        hex: hex,
                        a: rgba[3],
                    }
                }
            },
            showPicker() {
                document.addEventListener('click', this.documentClick);
                if(this.disablePicker) {
                  this.displayPicker = false;
                }else {
                  this.displayPicker = true;
                }
            },
            hidePicker() {
                document.removeEventListener('click', this.documentClick);
                this.displayPicker = false;
            },
            togglePicker() {
                this.displayPicker ? this.hidePicker() : this.showPicker();
            },
            updateFromInput() {
                this.updateColors(this.colorValue);
            },
            updateFromPicker(color) {
                this.colors = color;
                if(color.rgba.a === 1) {
                    this.colorValue = color.hex;
                }
                else {
                    this.colorValue = 'rgba(' + color.rgba.r + ', ' + color.rgba.g + ', ' + color.rgba.b + ', ' + color.rgba.a + ')';
                }
            },
            documentClick(e) {
                var el = this.$refs.colorpicker,
                    target = e.target;
                if(el !== target && !el.contains(target)) {
                    this.hidePicker()
                }
            }
        },
        watch: {
            color(val) {
                this.colorValue = val;
            },
            'colors.hex': function(val) {
                this.colorValue = val;
            },
            colorValue(val) {
                if(val) {
                    this.$emit("input", val) ;
                    this.updateColors(val);
                    this.$emit('input', val);
                    //document.body.style.background = val;
                }
            }
        }
    };
</script>
