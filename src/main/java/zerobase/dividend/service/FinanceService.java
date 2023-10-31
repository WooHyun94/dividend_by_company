package zerobase.dividend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.Dividend;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.scraper.Scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName){
        // 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                        .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));


        // 조회된 회사 ID로 배당금 정보 조회
        List< DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());
        List<Dividend> dividends = new ArrayList<>();

        for(var entity : dividendEntities){
            dividends.add(Dividend.builder()
                                .date(entity.getDate())
                                .dividend(entity.getDividend())
                                .build());
        }

        // 결과 조합 후 반환
        return new ScrapedResult(Company.builder()
                                        .ticker((company.getTicker()))
                                        .name(company.getName()).build(),
                                dividends);
    }
}
