package controller;

import domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LottoControllerTest {

    LottoController controller;

    @Test
    @DisplayName("구입 금액에 따른 로또 구매 개수 계산 기능")
    void calculateLottoQuantity() {
        int price = 14000;
        controller = new LottoController(price);
        assertThat(controller.getLottoQuantity()).isEqualTo(14);
    }

    @Test
    @DisplayName("로또 구매 개수대로 로또 자동 생성 기능")
    void makeAutoLottos() {
        int price = 5000;
        int quantity = 5;
        LottoInfo lottoInfo = LottoInfo.of(price, quantity);
        controller = new LottoController(lottoInfo);

        Lottos lottos = controller.initLottos(quantity);
        assertThat(lottos.getLottos().size()).isEqualTo(quantity);
    }

    @ParameterizedTest
    @DisplayName("당첨번호와 로또 번호 일치 개수 계산 기능 - 1개부터 5개까지")
    @CsvSource(value = {
            "1, 2, 3, 4, 5, 6:13, 8, 9, 10, 11, 12:0",
            "1, 2, 3, 4, 5, 6:1, 8, 9, 10, 11, 12:1",
            "1, 2, 3, 4, 5, 6:1, 2, 9, 10, 11, 12:2",
            "1, 2, 3, 4, 5, 6:1, 2, 3, 10, 11, 12:3",
            "1, 2, 3, 4, 5, 6:1, 2, 3, 4, 11, 12:4",
            "1, 2, 3, 4, 5, 6:1, 2, 3, 4, 5, 12:5",
            "1, 2, 3, 4, 5, 6:1, 2, 3, 4, 5, 6:6",
        }, delimiter = ':')
    void matchLottoNumbersDynamic(String winning, String test, int match) {
        LottoNumbers winningNumbers = new LottoNumbers()
                .createWinningNumbers(winning);

        LottoNumbers testNumbers = new LottoNumbers()
                .createWinningNumbers(test);
        Lotto testLotto = new Lotto(testNumbers);

        controller = new LottoController();

        assertThat(controller.matchLottoNumbers(winningNumbers, testLotto))
                .isEqualTo(match);
    }

    @Test
    @DisplayName("당첨 통계 계산 기능")
    void lottoStatistic() {
        String winning = "1, 2, 3, 4, 5, 6";
        LottoNumbers winningNumbers = new LottoNumbers()
                .createWinningNumbers(winning);

        Lottos lottos = initTestLottos();

        Map<Integer, Integer> result = new HashMap<>();
        result.put(3, 2);
        result.put(4, 0);
        result.put(5, 0);
        result.put(6, 1);

        controller = new LottoController();
        assertThat(controller.compileLottoStatistics(winningNumbers, lottos)).isEqualTo(result);
    }

    private Lottos initTestLottos() {
        String test1 = "1, 2, 3, 4, 5, 6";
        LottoNumbers testNumber1 = new LottoNumbers()
                .createWinningNumbers(test1);
        Lotto testLotto1 = new Lotto(testNumber1);

        String test2 = "1, 7, 8, 9, 10, 11";
        LottoNumbers testNumber2 = new LottoNumbers()
                .createWinningNumbers(test2);
        Lotto testLotto2 = new Lotto(testNumber2);

        String test3 = "1, 2, 3, 9, 10, 11";
        LottoNumbers testNumber3 = new LottoNumbers()
                .createWinningNumbers(test3);
        Lotto testLotto3 = new Lotto(testNumber3);

        String test4 = "1, 2, 3, 9, 10, 11";
        LottoNumbers testNumber4 = new LottoNumbers()
                .createWinningNumbers(test4);
        Lotto testLotto4 = new Lotto(testNumber4);


        return Lottos.from(Arrays.asList(testLotto1, testLotto2, testLotto3, testLotto4));
    }

    @Test
    @DisplayName("수익률 계산 기능")
    void calculateProfit() {
        LottoInfo lottoInfo = LottoInfo.of(4000, 4);

        String winning = "1, 2, 3, 4, 5, 6";
        LottoNumbers winningNumbers = new LottoNumbers()
                .createWinningNumbers(winning);

        Lottos lottos = initTestLottos();
        controller = new LottoController();
        Map<Integer, Integer> lottoStatistics = controller.compileLottoStatistics(winningNumbers, lottos);

        assertThat(controller.calculateProfit(lottoStatistics, lottoInfo.getPrice()))
                .isEqualTo(
                        (double) (LottoPrize.valueOf(3)*2
                                + LottoPrize.valueOf(6))
                                / (double) lottoInfo.getPrice()
                );
    }

}