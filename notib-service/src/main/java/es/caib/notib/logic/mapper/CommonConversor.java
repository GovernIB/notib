package es.caib.notib.logic.mapper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Mapper
public abstract class CommonConversor {

    @Named("optionalUser")
    public UsuariDto getOptionalUser(Optional<UsuariEntity> usuari) {
        if (usuari == null || !usuari.isPresent()) {
            return null;
        }
        return UsuariDto.builder().codi(usuari.get().getCodi()).nom(usuari.get().getNomSencer()).build();
    }

    @Named("optionalUserName")
    public String getOptionalUserName(Optional<UsuariEntity> usuari) {
        return usuari != null && usuari.isPresent() ? usuari.get().getNomSencer() : null;
    }

    @Named("optionalUserCode")
    public String getOptionalUserCode(Optional<UsuariEntity> usuari) {
        if (usuari == null || !usuari.isPresent()) {
            return null;
        }
        return usuari.get().getCodi();
    }

    @Named("optionalDate")
    public Date getOptionalDate(Optional<LocalDateTime> data) {
        if (data == null || !data.isPresent()) {
            return null;
        }
        return Date.from(data.get().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Named("procedimentCodiNom")
    public ProcSerDto getProcedimentCodiNom(ProcSerEntity procediment) {
        if (procediment == null) {
            return null;
        }
        return ProcSerDto.builder().codi(procediment.getCodi()).nom(procediment.getNom()).build();
    }
}
