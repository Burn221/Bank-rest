package com.example.bankcards.service.Impl;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.exceptions.ActivatedException;
import com.example.bankcards.exception.exceptions.ForbiddenTransactionException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.AesGcm;
import com.example.bankcards.util.Hmac;
import com.example.bankcards.util.Mask;
import com.example.bankcards.util.PanGenerator;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Класс сервиса для работы с картами, который реализует интерфейс CardService */
@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardMapper mapper;


    /** Создание карты админом
     * @param dto Приниамает CreateCardRequest dto который содержит поля для создания карты: userId, ownerName, currency
     * @throws NotFoundException если карта не найдена
     * @return объект CardResponse со следующими полями:
     *              <ul>
     *                  <li>id - идентификатор созданной карты</li>
     *                  <li>userId - идентификатор владельца</li>
     *                  <li>panMasked - скрытый маской PAN пользователя</li>
     *                  <li>expiryMonth - месяц истечения срока работы карты</li>
     *                  <li>expiryYear - месяц истечения срока работы карты</li>
     *                  <li>ownerName - имя владельца</li>
     *                  <li>currency - валюта</li>
     *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                  <li>balanceMinor - баланс на карте</li>
     *                  <li>createdAt - дата и время создания</li>
     *              </ul>*/
    @Override
    @Transactional
    public CardResponse createCardAdmin(CreateCardRequest dto) {

        User user= userRepository.findById(dto.userId())
                .orElseThrow(()-> new NotFoundException("User not found"));

        String generatedPan= PanGenerator.generatePan();

        byte[] panHash= Hmac.hmacSha256(generatedPan, Hmac.hmacKey());

        Card card= mapper.toEntity(dto, user);
        card.setUser(user);
        card.setPanEncrypted(AesGcm.encryptToBase64(generatedPan));
        card.setPanLast4(generatedPan.substring(generatedPan.length() - 4));
        card.setPanHash(panHash);

        card.setExpiryMonth((short) LocalDateTime.now().getMonthValue());
        card.setExpiryYear( (short) (LocalDateTime.now().getYear()+ 3) );
        card.setStatus(Status.ACTIVE);

        card.setCreatedAt(LocalDateTime.now());

        CardResponse response= mapper.toResponse(cardRepository.save(card));
        response.setPanMasked(Mask.mask(card.getPanLast4()));

        return response;
    }

    /** Обновить карту админом
     * @param cardId Принимает id карты которую нужно обновить
     * @param dto Принимает UpdateCardRequest dto которая содержит поле: ownerName для обновления
     * @throws NotFoundException если карта не найдена
     * @return объект CardResponse со следующими полями:
     *              <ul>
     *                  <li>id - идентификатор созданной карты</li>
     *                  <li>userId - идентификатор владельца</li>
     *                  <li>panMasked - скрытый маской PAN пользователя</li>
     *                  <li>expiryMonth - месяц истечения срока работы карты</li>
     *                  <li>expiryYear - месяц истечения срока работы карты</li>
     *                  <li>ownerName - имя владельца</li>
     *                  <li>currency - валюта</li>
     *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                  <li>balanceMinor - баланс на карте</li>
     *                  <li>createdAt - дата и время создания</li>
     *              </ul>*/

    @Override
    @Transactional
    public CardResponse updateCardAdmin (Long cardId, UpdateCardRequest dto) {

        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setOwnerName(dto.ownerName());

        CardResponse response= mapper.toResponse(cardRepository.save(card));
        response.setPanMasked(Mask.mask(card.getPanLast4()));

        return response;
    }

    /** Удаляет карту админом
     * @param cardId Принимает id карты для удаления
     * @throws NotFoundException если карта не найдена
     * @throws ActivatedException если карта disabled
     * Ничего не возвращает*/
    @Override
    @Transactional
    public void deleteCardAdmin(Long cardId) {

        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        if (card.getStatus().equals(Status.ACTIVE)) throw new ActivatedException("Card must be disabled to be deleted");
        cardRepository.deleteById(cardId);

    }
    /** Блокирует карту админом
     * @param cardId Принимает id карты для блокировки
     * @throws NotFoundException если карта не найдена
     * @return объект CardResponse со следующими полями:
           *              <ul>
           *                  <li>id - идентификатор созданной карты</li>
           *                  <li>userId - идентификатор владельца</li>
           *                  <li>panMasked - скрытый маской PAN пользователя</li>
           *                  <li>expiryMonth - месяц истечения срока работы карты</li>
           *                  <li>expiryYear - месяц истечения срока работы карты</li>
           *                  <li>ownerName - имя владельца</li>
           *                  <li>currency - валюта</li>
           *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
           *                  <li>balanceMinor - баланс на карте</li>
           *                  <li>createdAt - дата и время создания</li>
           *              </ul> */
    @Override
    @Transactional
    public CardResponse blockCardAdmin(Long cardId) {
        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setStatus(Status.BLOCKED);

        CardResponse response= mapper.toResponse(cardRepository.save(card));
        response.setPanMasked(Mask.mask(card.getPanLast4()));

        return response;
    }


    /** Активирует карту админом
     * @param cardId Принимает id карты для активирования
     * @throws NotFoundException если карта не найдена
     * @return объект CardResponse со следующими полями:
     *              <ul>
     *                  <li>id - идентификатор созданной карты</li>
     *                  <li>userId - идентификатор владельца</li>
     *                  <li>panMasked - скрытый маской PAN пользователя</li>
     *                  <li>expiryMonth - месяц истечения срока работы карты</li>
     *                  <li>expiryYear - месяц истечения срока работы карты</li>
     *                  <li>ownerName - имя владельца</li>
     *                  <li>currency - валюта</li>
     *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                  <li>balanceMinor - баланс на карте</li>
     *                  <li>createdAt - дата и время создания</li>
     *              </ul> */
    @Override
    @Transactional
    public CardResponse activateCardAdmin(Long cardId) {
        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setStatus(Status.ACTIVE);

        CardResponse response= mapper.toResponse(cardRepository.save(card));
        response.setPanMasked(Mask.mask(card.getPanLast4()));

        return response;
    }

    /** Показать все карты админу с пагинацией а также параметрами
     * @param pageable Принимает pageable для настройки отображения страниц и фильтрации/сортировки
     * @param userId Принимает id пользователя если необходим вывод карт определенного пользователя
     * @param status Принимает status если необходим выврд карт определенного статуса
     * @param last4 Принимает last4 если необходим вывод карт с совпадающими с этим параметром 4х последних цифр
     * @return Возвращает страницу Page состоящей из объектов CardResponse, которые состоят из:
     * <ul>
     *                        <li>id - идентификатор созданной карты</li>
     *                        <li>userId - идентификатор владельца</li>
     *                        <li>panMasked - скрытый маской PAN пользователя</li>
     *                        <li>expiryMonth - месяц истечения срока работы карты</li>
     *                        <li>expiryYear - месяц истечения срока работы карты</li>
     *                        <li>ownerName - имя владельца</li>
     *                        <li>currency - валюта</li>
     *                        <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                        <li>balanceMinor - баланс на карте</li>
     *                        <li>createdAt - дата и время создания</li>
     *                    </ul>*/


    @Transactional
    @Override
    @Cacheable(value = "allCardsAdmin", key = "#userId")
    public Page<CardResponse> showAllCardsAdmin(Pageable pageable, Long userId, Status status, String last4) {
        return cardRepository.findAdminFiltered(userId,status,last4,pageable)
                .map(card -> {
                    CardResponse dto = mapper.toResponse(card);
                    dto.setPanMasked(Mask.mask(card.getPanLast4()));
                    return dto;
                });
    }


    /** Получить карту по id админом
     * @param cardId Принимает id карты пользователя
     *  @throws NotFoundException если карта не найдена
     * @return ВозвращаеCardResponse, которые состоит из:
      * <ul>
      *                        <li>id - идентификатор созданной карты</li>
      *                        <li>userId - идентификатор владельца</li>
      *                        <li>panMasked - скрытый маской PAN пользователя</li>
      *                        <li>expiryMonth - месяц истечения срока работы карты</li>
      *                        <li>expiryYear - месяц истечения срока работы карты</li>
      *                        <li>ownerName - имя владельца</li>
      *                        <li>currency - валюта</li>
      *                        <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
      *                        <li>balanceMinor - баланс на карте</li>
      *                        <li>createdAt - дата и время создания</li>
      *                    </ul>*/
    @Override
    @Cacheable(value = "cardsAdmin", key = "#cardId")
    public CardResponse getByIdAdmin(Long cardId) {
        Card card=cardRepository.findById(cardId).orElseThrow(()-> new NotFoundException("Card not found"));
        CardResponse response= mapper.toResponse(card);

        response.setPanMasked(Mask.mask(card.getPanLast4()));
        return response;

    }

    /** Создать карту пользователем
     * @param dto Приниамает CreateCardRequest dto который содержит поля для создания карты: userId, ownerName, currency
     *  @throws NotFoundException если карта не найдена
     * @return объект CardResponse со следующими полями:
     *              <ul>
     *                  <li>id - идентификатор созданной карты</li>
     *                  <li>userId - идентификатор владельца</li>
     *                  <li>panMasked - скрытый маской PAN пользователя</li>
     *                  <li>panPLain - полный PAN пользователя (ВОЗВРАЩАЕТСЯ ТОЛЬКО ПОЛЬЗОВАТЕЛЮ)</li>
     *                  <li>expiryMonth - месяц истечения срока работы карты</li>
     *                  <li>expiryYear - месяц истечения срока работы карты</li>
     *                  <li>ownerName - имя владельца</li>
     *                  <li>currency - валюта</li>
     *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                  <li>balanceMinor - баланс на карте</li>
     *                  <li>createdAt - дата и время создания</li>
     *              </ul>*/
    @Transactional
    @Override
    public CardResponse createCardUser(Long userId, CreateCardRequest dto) {
        User user= userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if(!dto.ownerName().equals(user.getUsername())) throw new ForbiddenTransactionException("Owner name mismatch");

        String generatedPan= PanGenerator.generatePan();

        byte[] panHash= Hmac.hmacSha256(generatedPan, Hmac.hmacKey());

        Card card= mapper.toEntity(dto,user);

        card.setUser(user);
        card.setPanEncrypted(AesGcm.encryptToBase64(generatedPan));
        card.setPanLast4(generatedPan.substring(generatedPan.length() - 4));
        card.setPanHash(panHash);
        card.setExpiryMonth((short) LocalDateTime.now().getMonthValue());
        card.setExpiryYear( (short) (LocalDateTime.now().getYear()+ 3) );
        card.setStatus(Status.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());

        cardRepository.save(card);

        CardResponse response= mapper.toResponse(card);
        response.setPanPlain(AesGcm.decryptFromBase64(card.getPanEncrypted()));

        return response;
    }

    /** Показать все карты пользователем с пагинацией
     * @param pageable Принимает pageable для настройки отображения страниц и фильтрации/сортировки
     * @param userId Принимает id пользователя если необходим вывод карт определенного пользователя
     * @return Возвращает страницу Page состоящей из объектов CardResponse, которые состоят из:
     * <ul>
     *                        <li>id - идентификатор созданной карты</li>
     *                        <li>userId - идентификатор владельца</li>
     *                        <li>panMasked - скрытый маской PAN пользователя</li>
     *                        <li>panPLain - полный PAN пользователя (ВОЗВРАЩАЕТСЯ ТОЛЬКО ПОЛЬЗОВАТЕЛЮ)</li>
     *                        <li>expiryMonth - месяц истечения срока работы карты</li>
     *                        <li>expiryYear - месяц истечения срока работы карты</li>
     *                        <li>ownerName - имя владельца</li>
     *                        <li>currency - валюта</li>
     *                        <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
     *                        <li>balanceMinor - баланс на карте</li>
     *                        <li>createdAt - дата и время создания</li>
     *                    </ul>*/
    @Override
    public Page<CardResponse> showMyCardsUser(Long userId, Pageable pageable) {
        return cardRepository.findByUser_Id(userId,pageable)
                .map(card -> {
                    String last4 = card.getPanLast4();
                    CardResponse dto = mapper.toResponse(card);
                    dto.setPanMasked("**** **** **** " + last4);
                    dto.setPanPlain(AesGcm.decryptFromBase64(card.getPanEncrypted()));
                    return dto;
                });

    }


    /** Получить карту по id пользователем
      * @param cardId Принимает id карты пользователя
      *  @throws NotFoundException если карта не найдена
      * @param userId Принимает id пользователя
      * @return ВозвращаеCardResponse, которые состоит из:
      * <ul>
      *                        <li>id - идентификатор созданной карты</li>
      *                        <li>userId - идентификатор владельца</li>
      *                        <li>panMasked - скрытый маской PAN пользователя</li>
      *                        <li>panPLain - полный PAN пользователя (ВОЗВРАЩАЕТСЯ ТОЛЬКО ПОЛЬЗОВАТЕЛЮ)</li>
      *                        <li>expiryMonth - месяц истечения срока работы карты</li>
      *                        <li>expiryYear - месяц истечения срока работы карты</li>
      *                        <li>ownerName - имя владельца</li>
      *                        <li>currency - валюта</li>
      *                        <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
      *                        <li>balanceMinor - баланс на карте</li>
      *                        <li>createdAt - дата и время создания</li>
      *                    </ul>*/
    @Override
    @Cacheable(value = "cards", key = "#userId + ':' + #cardId")
    public CardResponse getMyCardUser(Long cardId, Long userId) {
        Card card= cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        CardResponse response= mapper.toResponse(card);
        response.setPanMasked(Mask.mask((card.getPanLast4())));
        response.setPanPlain(AesGcm.decryptFromBase64(card.getPanEncrypted()));

        return response;
    }

    /** Показать баланс пользователю
     * @param userId Принимает id пользователя
     * @param cardId Принимает id карты
     * @throws NotFoundException Если карта не найдена
     * @return Возвращает BalanceResponse с полями: balance, currency*/

    @Cacheable(value = "balance", key = "#userId + ':' + #cardId")    @Override
    public BalanceResponse showMyBalanceUser(Long userId, Long cardId) {

        return cardRepository.findBalanceUser(userId,cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));
    }

    /** Блокировка карты пользователем
     * @param userId Принимает id пользователя
     * @param cardId Принимает id карты
     * @throws NotFoundException если карта не найдена
     *    @return объект CardResponse со следующими полями:
      *              <ul>
      *                  <li>id - идентификатор созданной карты</li>
      *                  <li>userId - идентификатор владельца</li>
      *                  <li>panMasked - скрытый маской PAN пользователя</li>
      *                  <li>expiryMonth - месяц истечения срока работы карты</li>
      *                  <li>expiryYear - месяц истечения срока работы карты</li>
      *                  <li>ownerName - имя владельца</li>
      *                  <li>currency - валюта</li>
      *                  <li>status - текущий статус (ACTIVE, BLOCKED, EXPIRED)</li>
      *                  <li>balanceMinor - баланс на карте</li>
      *                  <li>createdAt - дата и время создания</li>
      *              </ul>*/
    @Override
    @Transactional
    @CacheEvict(value = "cards", key = "#userId + ':' + #cardId")

    public CardResponse blockRequestUser(Long userId, Long cardId, BlockCardRequest request) {

        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        if(!card.getUser().getId().equals(userId)) throw new IllegalArgumentException("User is incorrect or not found");

        if(!request.password().equals(request.confirmPassword())) throw new IllegalArgumentException("Passwords mismatch");

        card.setStatus(Status.BLOCKED);

        return mapper.toResponse(cardRepository.save(card));
    }
}
