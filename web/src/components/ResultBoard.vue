<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.loser === 'all'">
            All lose...
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id == parseInt($store.state.user.id)">
            Lose...
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.b_id == parseInt($store.state.user.id)">
            Lose...
        </div>
        <div class="result-board-text" v-else>
            Win!!!
        </div>
        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-warning btn-lg">
                重新匹配
            </button>

        </div>
    </div>
</template>

<script>

import { useStore } from 'vuex';

export default{
    setup()
    {
        const store = useStore();
        const restart = ()=>{
            store.commit("updateStatus","matching");
            store.commit("updateLoser","");
            store.commit("updateOpponent",{
                username:"我的对手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
        }

        return {
            restart,
        }
    }
}
</script>

<style scoped>
.result-board{
    height: 30%;
    width: 30%;
    background-color: rgba(56, 60, 62, 0.5);
    position: absolute;
    top:30%;
    left:35%;
}
.result-board-text{
    text-align: center;
    color: white;
    font-size: 600;
    font-style: italic;
    font-size: 50px;
    padding-top: 5vh;
}
.result-board-btn{
    text-align: center;
    padding-top: 7vh;
}
</style>