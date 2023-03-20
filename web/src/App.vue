<template>
  <NavBar></NavBar>
  <!-- <div>
   <div>Bot 昵称：</div>
   <div>Bot 战力：</div>
  </div> -->
  <router-view/>
</template>
<script>
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";
// import $ from "jquery";
// import {ref} from "vue";
import NavBar from './components/NavBar.vue';
export default{
  name:"App",
  components:{
    NavBar,
  }
}
</script>
<style >
body{
  background-image: url("./assets/image/bg.jpg");
  background-size: cover;
}
</style>
